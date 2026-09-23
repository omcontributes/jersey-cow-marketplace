package com.example.cowmarketplace.dto.request;

import com.example.cowmarketplace.entity.Breed;
import jakarta.validation.constraints.Positive;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CowUpdateRequest {

    private String name;

    private Breed breed;

    private Integer age;

    private String gender;

    @Positive(message = "Price must be greater than zero")
    private BigDecimal price;

    private String description;

    @Positive(message = "Milk per day must be positive")
    private Double milkPerDay;

    private Integer lactationNumber;

    private String vaccinationStatus;

    private String healthStatus;

    private String location;
}