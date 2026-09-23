package com.example.cowmarketplace.mapper;

import com.example.cowmarketplace.dto.response.UserResponse;
import com.example.cowmarketplace.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .whatsappNumber(user.getWhatsappNumber())
                .city(user.getCity())
                .state(user.getState())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}