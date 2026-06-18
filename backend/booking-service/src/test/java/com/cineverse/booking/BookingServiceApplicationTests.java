package com.cineverse.booking;

import com.cineverse.booking.dto.BookingRequest;
import com.cineverse.booking.model.*;
import com.cineverse.booking.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookingServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TheatreRepository theatreRepository;

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        seatRepository.deleteAll();
        showRepository.deleteAll();
        screenRepository.deleteAll();
        theatreRepository.deleteAll();
    }

    @Test
    void testBookingWorkflow() throws Exception {
        // 1. Create a Theatre
        Theatre theatre = new Theatre("PVR Cinemas", "Delhi");
        String theatreJson = mockMvc.perform(post("/theatres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(theatre)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.name", is("PVR Cinemas")))
                .andExpect(jsonPath("$.data.location", is("Delhi")))
                .andReturn().getResponse().getContentAsString();
        
        Long theatreId = objectMapper.readTree(theatreJson).get("data").get("id").asLong();

        // 2. Create a Screen
        Screen screen = new Screen("Audi 1", 100, theatreId);
        String screenJson = mockMvc.perform(post("/screens")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(screen)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.name", is("Audi 1")))
                .andExpect(jsonPath("$.data.capacity", is(100)))
                .andReturn().getResponse().getContentAsString();
        
        Long screenId = objectMapper.readTree(screenJson).get("data").get("id").asLong();

        // 3. Create a Show
        Show show = new Show("M1", screenId, "10:00 AM");
        String showJson = mockMvc.perform(post("/shows")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(show)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.movieId", is("M1")))
                .andExpect(jsonPath("$.data.startTime", is("10:00 AM")))
                .andReturn().getResponse().getContentAsString();

        Long showId = objectMapper.readTree(showJson).get("data").get("id").asLong();

        // 3a. Validate Show Slot overlap
        Show overlapShow = new Show("M2", screenId, "10:00 AM");
        mockMvc.perform(post("/shows")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(overlapShow)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", containsString("Time slot collision")));

        // 4. Retrieve Seat layout (which triggers grid initialization)
        mockMvc.perform(get("/seats/" + showId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data", hasSize(70))) // Rows A to G, columns 1-10 = 70 seats
                .andExpect(jsonPath("$.data[0].seatCode", is("A1")))
                .andExpect(jsonPath("$.data[0].status", is("AVAILABLE")));

        // 5. Create a Booking lock on A1 and A2
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setShowId(showId);
        bookingRequest.setUserId("user-101");
        bookingRequest.setSeatCodes(Arrays.asList("A1", "A2"));

        String bookingJson = mockMvc.perform(post("/booking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.status", is("LOCKED")))
                .andExpect(jsonPath("$.data.amount", is(300.0))) // Regular seat price is 150 each
                .andReturn().getResponse().getContentAsString();

        Long bookingId = objectMapper.readTree(bookingJson).get("data").get("id").asLong();

        // 6. Verify seats are now LOCKED
        mockMvc.perform(get("/seats/" + showId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[?(@.seatCode=='A1')].status", contains("LOCKED")))
                .andExpect(jsonPath("$.data[?(@.seatCode=='A2')].status", contains("LOCKED")))
                .andExpect(jsonPath("$.data[?(@.seatCode=='A3')].status", contains("AVAILABLE")));

        // 7. Verify concurrent double-booking is rejected
        BookingRequest doubleBookingRequest = new BookingRequest();
        doubleBookingRequest.setShowId(showId);
        doubleBookingRequest.setUserId("user-102");
        doubleBookingRequest.setSeatCodes(Arrays.asList("A2", "A3"));

        mockMvc.perform(post("/booking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(doubleBookingRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", containsString("temporarily locked")));

        // 8. Confirm the booking
        mockMvc.perform(post("/booking/" + bookingId + "/confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.status", is("CONFIRMED")));

        // 9. Verify seats are now BOOKED
        mockMvc.perform(get("/seats/" + showId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[?(@.seatCode=='A1')].status", contains("BOOKED")))
                .andExpect(jsonPath("$.data[?(@.seatCode=='A2')].status", contains("BOOKED")));

        // 10. Attempt booking booked seat
        BookingRequest bookedRequest = new BookingRequest();
        bookedRequest.setShowId(showId);
        bookedRequest.setUserId("user-103");
        bookedRequest.setSeatCodes(Arrays.asList("A1"));

        mockMvc.perform(post("/booking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookedRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", containsString("already booked")));
    }
}
