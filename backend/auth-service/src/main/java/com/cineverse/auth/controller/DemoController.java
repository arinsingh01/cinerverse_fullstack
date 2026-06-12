package com.cineverse.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class DemoController {

    @GetMapping("/tickets/book")
    public ResponseEntity<Map<String, Object>> bookTickets() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Ticket booking successful - Authorized as USER");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/shows/manage")
    public ResponseEntity<Map<String, Object>> manageShows() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Shows managed successfully - Authorized as THEATRE_OWNER or ADMIN");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/manage")
    public ResponseEntity<Map<String, Object>> manageUsers() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Users managed successfully - Authorized as ADMIN");
        return ResponseEntity.ok(response);
    }
}
