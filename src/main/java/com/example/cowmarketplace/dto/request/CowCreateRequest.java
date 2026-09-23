package com.example.cowmarketplace.dto.request;

import com.example.cowmarketplace.entity.Breed;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CowCreateRequest {

    @NotBlank(message = "Cow name is required")
    private String name;

    @NotNull(message = "Breed is required")
    private Breed breed;

    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age cannot be negative")
    @Max(value = 25, message = "Age seems unrealistic")
    private Integer age;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    private BigDecimal price;

    private String description;

    @Positive(message = "Milk per day must be positive")
    private Double milkPerDay;

    private Integer lactationNumber;

    private String vaccinationStatus;

    private String healthStatus;

    @NotBlank(message = "Location is required")
    private String location;
}