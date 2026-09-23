package com.example.cowmarketplace.service.impl;

import com.example.cowmarketplace.dto.request.LoginRequest;
import com.example.cowmarketplace.dto.request.RegisterRequest;
import com.example.cowmarketplace.dto.response.LoginResponse;
import com.example.cowmarketplace.dto.response.UserResponse;
import com.example.cowmarketplace.entity.User;
import com.example.cowmarketplace.exception.InvalidCredentialsException;
import com.example.cowmarketplace.exception.MessageHelper;
import com.example.cowmarketplace.exception.UserAlreadyExistsException;
import com.example.cowmarketplace.mapper.UserMapper;
import com.example.cowmarketplace.repository.UserRepository;
import com.example.cowmarketplace.security.JwtUtil;
import com.example.cowmarketplace.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final MessageHelper messageHelper;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(
                    messageHelper.get("user.email.exists", request.getEmail()));
        }

        if (userRepository.existsByMobileNumber(request.getMobileNumber())) {
            throw new UserAlreadyExistsException(
                    messageHelper.get("user.mobile.exists", request.getMobileNumber()));
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .mobileNumber(request.getMobileNumber())
                .whatsappNumber(request.getWhatsappNumber())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .role(request.getRole())
                .build();

        User savedUser = userRepository.save(user);

        return userMapper.toUserResponse(savedUser);
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole().name());

        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .name(user.getFullName())
                .role(user.getRole().name())
                .build();
    }
}