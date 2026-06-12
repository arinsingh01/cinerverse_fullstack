package com.cineverse.auth;

import com.cineverse.auth.dto.LoginRequest;
import com.cineverse.auth.dto.RegisterRequest;
import com.cineverse.auth.model.Role;
import com.cineverse.auth.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }

    @Test
    public void testUserRegistrationValidation() throws Exception {
        // Register request with short password & invalid email
        RegisterRequest request = RegisterRequest.builder()
                .name("Test User")
                .email("invalid-email")
                .password("123")
                .role(Role.USER)
                .build();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Invalid email format")))
                .andExpect(jsonPath("$.message", containsString("Password must be at least 6 characters long")));
    }

    @Test
    public void testSuccessfulRegistrationAndLogin() throws Exception {
        // 1. Register
        RegisterRequest register = RegisterRequest.builder()
                .name("Arin Singh")
                .email("arin@cineverse.com")
                .password("password123")
                .role(Role.USER)
                .build();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"));

        // 2. Login
        LoginRequest login = LoginRequest.builder()
                .email("arin@cineverse.com")
                .password("password123")
                .build();

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseString).get("token").asText();
        assertNotNull(token);
    }

    @Test
    public void testLoginFailureErrorResponse() throws Exception {
        LoginRequest login = LoginRequest.builder()
                .email("nonexistent@cineverse.com")
                .password("wrongpassword")
                .build();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    public void testRoleBasedAccessControl() throws Exception {
        // Register 3 users with different roles: USER, THEATRE_OWNER, ADMIN
        RegisterRequest userReg = RegisterRequest.builder()
                .name("Regular User")
                .email("user@cineverse.com")
                .password("password123")
                .role(Role.USER)
                .build();
        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userReg)));

        RegisterRequest ownerReg = RegisterRequest.builder()
                .name("Theatre Owner")
                .email("owner@cineverse.com")
                .password("password123")
                .role(Role.THEATRE_OWNER)
                .build();
        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(ownerReg)));

        RegisterRequest adminReg = RegisterRequest.builder()
                .name("Admin User")
                .email("admin@cineverse.com")
                .password("password123")
                .role(Role.ADMIN)
                .build();
        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(adminReg)));

        // Login as USER and get token
        String userToken = getLoginToken("user@cineverse.com", "password123");
        // Login as THEATRE_OWNER and get token
        String ownerToken = getLoginToken("owner@cineverse.com", "password123");
        // Login as ADMIN and get token
        String adminToken = getLoginToken("admin@cineverse.com", "password123");

        // --- Verify USER access rules ---
        // Book Tickets: Yes
        mockMvc.perform(get("/tickets/book").header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
        // Manage Shows: No
        mockMvc.perform(get("/shows/manage").header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("Access denied - Insufficient privileges"));
        // Manage Users: No
        mockMvc.perform(get("/users/manage").header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());

        // --- Verify THEATRE_OWNER access rules ---
        // Book Tickets: No
        mockMvc.perform(get("/tickets/book").header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isForbidden());
        // Manage Shows: Yes
        mockMvc.perform(get("/shows/manage").header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk());
        // Manage Users: No
        mockMvc.perform(get("/users/manage").header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isForbidden());

        // --- Verify ADMIN access rules ---
        // Book Tickets: No
        mockMvc.perform(get("/tickets/book").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());
        // Manage Shows: Yes
        mockMvc.perform(get("/shows/manage").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
        // Manage Users: Yes
        mockMvc.perform(get("/users/manage").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    private String getLoginToken(String email, String password) throws Exception {
        LoginRequest login = LoginRequest.builder()
                .email(email)
                .password(password)
                .build();

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }
}
