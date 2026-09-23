package com.example.cowmarketplace.dto.request;

import com.example.cowmarketplace.entity.Breed;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CowSearchRequest {

    private Breed breed;
    private String city;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer minAge;
    private Integer maxAge;
    private Double minMilkPerDay;
}