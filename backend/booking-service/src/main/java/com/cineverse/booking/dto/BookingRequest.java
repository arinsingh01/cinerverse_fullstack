package com.cineverse.booking.dto;

import java.util.List;

public class BookingRequest {
    private String userId;
    private Long showId;
    private List<String> seatCodes;

    public BookingRequest() {}

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

    public List<String> getSeatCodes() {
        return seatCodes;
    }

    public void setSeatCodes(List<String> seatCodes) {
        this.seatCodes = seatCodes;
    }
}
