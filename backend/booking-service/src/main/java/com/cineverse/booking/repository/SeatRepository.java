package com.cineverse.booking.repository;

import com.cineverse.booking.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByShowId(Long showId);
    List<Seat> findByShowIdAndSeatCodeIn(Long showId, List<String> seatCodes);
}
