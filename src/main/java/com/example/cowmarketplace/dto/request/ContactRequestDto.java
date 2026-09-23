package com.example.cowmarketplace.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactRequestDto {

    @NotNull(message = "Cow id is required")
    private Long cowId;

    private String message;
}