package com.example.cowmarketplace.controller.service;

import com.example.cowmarketplace.dto.request.LoginRequest;
import com.example.cowmarketplace.dto.request.RegisterRequest;
import com.example.cowmarketplace.dto.response.LoginResponse;
import com.example.cowmarketplace.dto.response.UserResponse;
import com.example.cowmarketplace.entity.Role;
import com.example.cowmarketplace.entity.User;
import com.example.cowmarketplace.exception.InvalidCredentialsException;
import com.example.cowmarketplace.exception.MessageHelper;
import com.example.cowmarketplace.exception.UserAlreadyExistsException;
import com.example.cowmarketplace.mapper.UserMapper;
import com.example.cowmarketplace.repository.UserRepository;
import com.example.cowmarketplace.security.JwtUtil;
import com.example.cowmarketplace.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private MessageHelper messageHelper;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private User savedUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setFullName("Ramesh Patil");
        registerRequest.setEmail("ramesh@gmail.com");
        registerRequest.setPassword("password123");
        registerRequest.setMobileNumber("9876543210");
        registerRequest.setRole(Role.FARMER);

        savedUser = User.builder()
                .id(1L)
                .fullName("Ramesh Patil")
                .email("ramesh@gmail.com")
                .password("hashed-password")
                .mobileNumber("9876543210")
                .role(Role.FARMER)
                .build();
    }

    @Test
    void register_withNewEmail_savesUserSuccessfully() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByMobileNumber(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toUserResponse(savedUser)).thenReturn(
                UserResponse.builder().id(1L).fullName("Ramesh Patil").email("ramesh@gmail.com").build());

        UserResponse response = authService.register(registerRequest);

        assertThat(response.getEmail()).isEqualTo("ramesh@gmail.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_withDuplicateEmail_throwsUserAlreadyExistsException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        when(messageHelper.get(anyString(), any())).thenReturn("Email already registered");

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(UserAlreadyExistsException.class);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_withCorrectCredentials_returnsTokenAndUserInfo() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("ramesh@gmail.com");
        loginRequest.setPassword("password123");

        when(userRepository.findByEmail("ramesh@gmail.com")).thenReturn(Optional.of(savedUser));
        when(passwordEncoder.matches("password123", "hashed-password")).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), any(), anyString())).thenReturn("mocked-jwt-token");

        LoginResponse response = authService.login(loginRequest);

        assertThat(response.getAccessToken()).isEqualTo("mocked-jwt-token");
        assertThat(response.getRole()).isEqualTo("FARMER");
    }

    @Test
    void login_withWrongPassword_throwsInvalidCredentialsException() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("ramesh@gmail.com");
        loginRequest.setPassword("wrongpassword");

        when(userRepository.findByEmail("ramesh@gmail.com")).thenReturn(Optional.of(savedUser));
        when(passwordEncoder.matches("wrongpassword", "hashed-password")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void login_withNonExistentEmail_throwsInvalidCredentialsException() {
        when(userRepository.findByEmail("nobody@gmail.com")).thenReturn(Optional.empty());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("nobody@gmail.com");
        loginRequest.setPassword("anything");

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}