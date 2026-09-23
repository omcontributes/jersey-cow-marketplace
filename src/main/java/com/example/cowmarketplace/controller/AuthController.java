package com.example.cowmarketplace.controller;

import com.example.cowmarketplace.dto.request.LoginRequest;
import com.example.cowmarketplace.dto.request.RegisterRequest;
import com.example.cowmarketplace.dto.response.LoginResponse;
import com.example.cowmarketplace.dto.response.UserResponse;
import com.example.cowmarketplace.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Registration and login endpoints")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new farmer or buyer account")
    @SecurityRequirements // overrides the global bearer requirement — this endpoint needs no token
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Login and receive a JWT access token")
    @SecurityRequirements
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}