package com.cineverse.booking.service;

import com.cineverse.booking.dto.BookingRequest;
import com.cineverse.booking.model.*;
import com.cineverse.booking.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final TheatreRepository theatreRepository;
    private final ScreenRepository screenRepository;
    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    private final RestTemplate restTemplate;

    @Value("${services.auth-service.url}")
    private String authServiceUrl;

    public BookingService(TheatreRepository theatreRepository,
                          ScreenRepository screenRepository,
                          ShowRepository showRepository,
                          SeatRepository seatRepository,
                          BookingRepository bookingRepository,
                          RestTemplate restTemplate) {
        this.theatreRepository = theatreRepository;
        this.screenRepository = screenRepository;
        this.showRepository = showRepository;
        this.seatRepository = seatRepository;
        this.bookingRepository = bookingRepository;
        this.restTemplate = restTemplate;
    }


    public List<Theatre> getAllTheatres() {
        return theatreRepository.findAll();
    }

    public Theatre createTheatre(Theatre theatre) {
        return theatreRepository.save(theatre);
    }

    public Screen createScreen(Screen screen) {
        return screenRepository.save(screen);
    }

    public Show createShow(Show show) {
        // Validation: No overlapping shows on the same screen (highly realistic check)
        List<Show> existingShows = showRepository.findByScreenId(show.getScreenId());
        for (Show s : existingShows) {
            if (s.getStartTime().equalsIgnoreCase(show.getStartTime())) {
                throw new RuntimeException("Time slot collision: A show is already scheduled on this screen at " + show.getStartTime());
            }
        }
        return showRepository.save(show);
    }

    public List<Show> getAllShows() {
        return showRepository.findAll();
    }

    @Transactional
    public List<Seat> getSeatsForShow(Long showId) {
        List<Seat> seats = seatRepository.findByShowId(showId);
        
        // Auto-initialize seating grid if none exists
        if (seats.isEmpty()) {
            seats = new ArrayList<>();
            List<String> rows = Arrays.asList("A", "B", "C", "D", "E", "F", "G");
            for (String row : rows) {
                for (int i = 1; i <= 10; i++) {
                    String code = row + i;
                    String type = row.compareTo("D") <= 0 ? "REGULAR" : "VIP";
                    int price = type.equals("REGULAR") ? 150 : 250;
                    seats.add(new Seat(showId, code, type, price, "AVAILABLE"));
                }
            }
            seats = seatRepository.saveAll(seats);
        } else {
            // Check and release expired temporary locks (older than 5 minutes)
            LocalDateTime threshold = LocalDateTime.now().minusMinutes(5);
            boolean updated = false;
            for (Seat seat : seats) {
                if ("LOCKED".equals(seat.getStatus()) && seat.getLockedAt() != null && seat.getLockedAt().isBefore(threshold)) {
                    seat.setStatus("AVAILABLE");
                    seat.setLockedAt(null);
                    updated = true;
                }
            }
            if (updated) {
                seatRepository.saveAll(seats);
            }
        }
        return seats;
    }

    @Transactional
    public Booking createBooking(BookingRequest request) {
        // Validate user existence via Auth Service
        try {
            String url = authServiceUrl + "/auth/users/" + request.getUserId();
            restTemplate.getForObject(url, Object.class);
        } catch (Exception e) {
            throw new RuntimeException("Validation collision: User not found or invalid user ID: " + request.getUserId());
        }

        Long showId = request.getShowId();
        List<String> seatCodes = request.getSeatCodes();

        
        // Fetch target seats
        List<Seat> targetSeats = seatRepository.findByShowIdAndSeatCodeIn(showId, seatCodes);
        if (targetSeats.size() != seatCodes.size()) {
            throw new RuntimeException("Validation error: One or more requested seats do not exist.");
        }

        LocalDateTime threshold = LocalDateTime.now().minusMinutes(5);

        // Check if any seat is already booked or active locked
        for (Seat seat : targetSeats) {
            if ("BOOKED".equals(seat.getStatus())) {
                throw new RuntimeException("Booking collision: Seat " + seat.getSeatCode() + " is already booked.");
            }
            if ("LOCKED".equals(seat.getStatus()) && seat.getLockedAt() != null && seat.getLockedAt().isAfter(threshold)) {
                throw new RuntimeException("Booking collision: Seat " + seat.getSeatCode() + " is temporarily locked by another checkout session.");
            }
        }

        // Apply temporary locks
        double totalAmount = 0.0;
        for (Seat seat : targetSeats) {
            seat.setStatus("LOCKED");
            seat.setLockedAt(LocalDateTime.now());
            totalAmount += seat.getPrice();
        }
        seatRepository.saveAll(targetSeats);

        // Create booking record
        String codesString = String.join(", ", seatCodes);
        Booking booking = new Booking(request.getUserId(), showId, codesString, totalAmount, "LOCKED");
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking confirmBooking(Long bookingId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new RuntimeException("Booking record not found with ID: " + bookingId);
        }
        Booking booking = bookingOpt.get();

        if (!"LOCKED".equals(booking.getStatus())) {
            throw new RuntimeException("Invalid state: Booking status is " + booking.getStatus());
        }

        // Check lock expiration
        if (booking.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(5))) {
            booking.setStatus("EXPIRED");
            bookingRepository.save(booking);

            // Release seats
            List<String> seatCodes = Arrays.asList(booking.getSeatCodes().split(", "));
            List<Seat> seats = seatRepository.findByShowIdAndSeatCodeIn(booking.getShowId(), seatCodes);
            for (Seat s : seats) {
                s.setStatus("AVAILABLE");
                s.setLockedAt(null);
            }
            seatRepository.saveAll(seats);

            throw new RuntimeException("Transaction expired: Seating reservation timeout occurred.");
        }

        // Confirm booking and update seat statuses
        booking.setStatus("CONFIRMED");
        bookingRepository.save(booking);

        List<String> seatCodes = Arrays.asList(booking.getSeatCodes().split(", "));
        List<Seat> seats = seatRepository.findByShowIdAndSeatCodeIn(booking.getShowId(), seatCodes);
        for (Seat s : seats) {
            s.setStatus("BOOKED");
            s.setLockedAt(null);
        }
        seatRepository.saveAll(seats);

        return booking;
    }

    @Transactional
    public Booking cancelBooking(Long bookingId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new RuntimeException("Booking record not found with ID: " + bookingId);
        }
        Booking booking = bookingOpt.get();

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);

        // Release seats
        List<String> seatCodes = Arrays.asList(booking.getSeatCodes().split(", "));
        List<Seat> seats = seatRepository.findByShowIdAndSeatCodeIn(booking.getShowId(), seatCodes);
        for (Seat s : seats) {
            s.setStatus("AVAILABLE");
            s.setLockedAt(null);
        }
        seatRepository.saveAll(seats);

        return booking;
    }
}
