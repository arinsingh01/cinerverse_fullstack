package com.cineverse.movie.dto;

import java.util.List;

public class ReviewListResponse {
    private String status;
    private String message;
    private List<ReviewDto> data;

    public ReviewListResponse() {}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ReviewDto> getData() {
        return data;
    }

    public void setData(List<ReviewDto> data) {
        this.data = data;
    }
}
