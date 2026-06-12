package com.cineverse.auth.dto;

public class ErrorResponse {
    private String message;
    private int status;

    public ErrorResponse() {}

    public ErrorResponse(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public static ErrorResponseBuilder builder() {
        return new ErrorResponseBuilder();
    }

    public static class ErrorResponseBuilder {
        private String message;
        private int status;

        ErrorResponseBuilder() {}

        public ErrorResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public ErrorResponseBuilder status(int status) {
            this.status = status;
            return this;
        }

        public ErrorResponse build() {
            return new ErrorResponse(message, status);
        }
    }
}
