package com.cineverse.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequest {

    @NotBlank(message = "Token is required")
    private String token;

    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "New password must be at least 6 characters long")
    private String newPassword;

    public ResetPasswordRequest() {}

    public ResetPasswordRequest(String token, String newPassword) {
        this.token = token;
        this.newPassword = newPassword;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

    public static ResetPasswordRequestBuilder builder() {
        return new ResetPasswordRequestBuilder();
    }

    public static class ResetPasswordRequestBuilder {
        private String token;
        private String newPassword;

        ResetPasswordRequestBuilder() {}

        public ResetPasswordRequestBuilder token(String token) {
            this.token = token;
            return this;
        }

        public ResetPasswordRequestBuilder newPassword(String newPassword) {
            this.newPassword = newPassword;
            return this;
        }

        public ResetPasswordRequest build() {
            return new ResetPasswordRequest(token, newPassword);
        }
    }
}
