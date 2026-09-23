package com.example.cowmarketplace.service;

import com.example.cowmarketplace.dto.request.LoginRequest;
import com.example.cowmarketplace.dto.request.RegisterRequest;
import com.example.cowmarketplace.dto.response.LoginResponse;
import com.example.cowmarketplace.dto.response.UserResponse;

public interface AuthService {
    UserResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
}