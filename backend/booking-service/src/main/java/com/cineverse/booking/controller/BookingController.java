package com.cineverse.booking.controller;

import com.cineverse.booking.dto.BookingRequest;
import com.cineverse.booking.dto.StandardResponse;
import com.cineverse.booking.model.*;
import com.cineverse.booking.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/theatres")
    public ResponseEntity<StandardResponse<List<Theatre>>> getAllTheatres() {
        List<Theatre> theatres = bookingService.getAllTheatres();
        return ResponseEntity.ok(StandardResponse.success("Theatres fetched successfully", theatres));
    }

    @PostMapping("/theatres")
    public ResponseEntity<StandardResponse<Theatre>> createTheatre(@RequestBody Theatre theatre) {
        Theatre saved = bookingService.createTheatre(theatre);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponse.success("Theatre created successfully", saved));
    }

    @PostMapping("/screens")
    public ResponseEntity<StandardResponse<Screen>> createScreen(@RequestBody Screen screen) {
        Screen saved = bookingService.createScreen(screen);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponse.success("Screen created successfully", saved));
    }


    @PostMapping("/shows")
    public ResponseEntity<StandardResponse<Show>> createShow(@RequestBody Show show) {
        Show saved = bookingService.createShow(show);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponse.success("Show created successfully", saved));
    }

    @GetMapping("/shows")
    public ResponseEntity<StandardResponse<List<Show>>> getAllShows() {
        List<Show> shows = bookingService.getAllShows();
        return ResponseEntity.ok(StandardResponse.success("Shows fetched successfully", shows));
    }

    @GetMapping("/seats/{showId}")
    public ResponseEntity<StandardResponse<List<Seat>>> getSeatsForShow(@PathVariable Long showId) {
        List<Seat> seats = bookingService.getSeatsForShow(showId);
        return ResponseEntity.ok(StandardResponse.success("Seating layout fetched successfully", seats));
    }

    @PostMapping("/booking")
    public ResponseEntity<StandardResponse<Booking>> createBooking(@RequestBody BookingRequest request) {
        Booking booking = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponse.success("Booking initialized and seats locked", booking));
    }

    @PostMapping("/booking/{bookingId}/confirm")
    public ResponseEntity<StandardResponse<Booking>> confirmBooking(@PathVariable Long bookingId) {
        Booking booking = bookingService.confirmBooking(bookingId);
        return ResponseEntity.ok(StandardResponse.success("Booking confirmed successfully", booking));
    }

    @PostMapping("/booking/{bookingId}/cancel")
    public ResponseEntity<StandardResponse<Booking>> cancelBooking(@PathVariable Long bookingId) {
        Booking booking = bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok(StandardResponse.success("Booking cancelled successfully", booking));
    }
}
