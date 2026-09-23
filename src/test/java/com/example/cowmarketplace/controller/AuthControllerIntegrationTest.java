package com.example.cowmarketplace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.cowmarketplace.dto.request.LoginRequest;
import com.example.cowmarketplace.dto.request.RegisterRequest;
import com.example.cowmarketplace.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_withValidData_returns201AndUserResponse() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Ramesh Patil");
        request.setEmail("ramesh.integration@gmail.com");
        request.setPassword("password123");
        request.setMobileNumber("9876543299");
        request.setRole(Role.FARMER);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("ramesh.integration@gmail.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void register_withDuplicateEmail_returns409() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Suresh Jadhav");
        request.setEmail("suresh.duplicate@gmail.com");
        request.setPassword("password123");
        request.setMobileNumber("9876543288");
        request.setRole(Role.FARMER);

        // First registration succeeds
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Second registration with same email fails
        request.setMobileNumber("9876543277"); // different mobile, same email
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("USER_ALREADY_EXISTS"));
    }

    @Test
    void register_withInvalidEmail_returns400WithFieldErrors() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Test User");
        request.setEmail("not-an-email");
        request.setPassword("password123");
        request.setMobileNumber("9876543266");
        request.setRole(Role.FARMER);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors.email").exists());
    }

    @Test
    void login_withCorrectCredentials_returns200AndToken() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFullName("Login Test User");
        registerRequest.setEmail("logintest@gmail.com");
        registerRequest.setPassword("password123");
        registerRequest.setMobileNumber("9876543255");
        registerRequest.setRole(Role.FARMER);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("logintest@gmail.com");
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.role").value("FARMER"));
    }

    @Test
    void login_withWrongPassword_returns401() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFullName("Wrong Pass Test");
        registerRequest.setEmail("wrongpass@gmail.com");
        registerRequest.setPassword("password123");
        registerRequest.setMobileNumber("9876543244");
        registerRequest.setRole(Role.FARMER);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("wrongpass@gmail.com");
        loginRequest.setPassword("totallyWrongPassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("INVALID_CREDENTIALS"));
    }
}