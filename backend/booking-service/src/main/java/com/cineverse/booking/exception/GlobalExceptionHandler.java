package com.cineverse.booking.exception;

import com.cineverse.booking.dto.StandardResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardResponse<Void>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(StandardResponse.error(ex.getMessage()));
    }
}
