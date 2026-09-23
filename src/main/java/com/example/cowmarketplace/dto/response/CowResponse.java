package com.example.cowmarketplace.dto.response;

import com.example.cowmarketplace.entity.Breed;
import com.example.cowmarketplace.entity.CowStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CowResponse {

    private Long id;
    private String name;
    private Breed breed;
    private Integer age;
    private String gender;
    private BigDecimal price;
    private String location;
    private CowStatus status;
    private String primaryImageUrl; // first image only — for list/card views
}