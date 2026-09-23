package com.example.cowmarketplace.dto.response;

import com.example.cowmarketplace.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String fullName;
    private String email;
    private String mobileNumber;
    private String whatsappNumber;
    private String city;
    private String state;
    private Role role;
    private LocalDateTime createdAt;
}