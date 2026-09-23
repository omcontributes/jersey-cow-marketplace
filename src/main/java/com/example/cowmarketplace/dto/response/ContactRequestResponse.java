package com.example.cowmarketplace.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ContactRequestResponse {

    private Long id;
    private Long cowId;
    private String cowName;
    private String buyerName;
    private String buyerMobile;
    private String message;
    private LocalDateTime createdAt;
}