package com.cineverse.movie.dto;

public class StandardResponse<T> {
    private String status;
    private String message;
    private T data;

    public StandardResponse() {}

    public StandardResponse(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> StandardResponse<T> success(String message, T data) {
        return new StandardResponse<>("success", message, data);
    }

    public static <T> StandardResponse<T> error(String message) {
        return new StandardResponse<>("error", message, null);
    }

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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
