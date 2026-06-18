package com.cineverse.booking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "show_id")
    private Long showId;

    @Column(name = "seat_codes")
    private String seatCodes; // Comma separated seat codes e.g. "A1, A2"

    private Double amount;
    private String status; // INITIATED, LOCKED, CONFIRMED, CANCELLED, EXPIRED
    private LocalDateTime createdAt;

    public Booking() {}

    public Booking(String userId, Long showId, String seatCodes, Double amount, String status) {
        this.userId = userId;
        this.showId = showId;
        this.seatCodes = seatCodes;
        this.amount = amount;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getShowId() {
        return showId;
    }

    public void setShowId(Long showId) {
        this.showId = showId;
    }

    public String getSeatCodes() {
        return seatCodes;
    }

    public void setSeatCodes(String seatCodes) {
        this.seatCodes = seatCodes;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
