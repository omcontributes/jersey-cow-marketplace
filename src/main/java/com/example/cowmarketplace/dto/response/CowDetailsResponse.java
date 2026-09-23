package com.example.cowmarketplace.dto.response;

import com.example.cowmarketplace.entity.Breed;
import com.example.cowmarketplace.entity.CowStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CowDetailsResponse {

    private Long id;
    private String name;
    private Breed breed;
    private Integer age;
    private String gender;
    private BigDecimal price;
    private String description;
    private Double milkPerDay;
    private Integer lactationNumber;
    private String vaccinationStatus;
    private String healthStatus;
    private String location;
    private CowStatus status;
    private LocalDateTime createdAt;

    private String farmerName;
    private String farmerMobile;
    private String farmerWhatsapp;

    private List<CowImageResponse> images;

    private String callLink;
    private String whatsappLink;
}