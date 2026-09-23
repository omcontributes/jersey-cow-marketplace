package com.example.cowmarketplace.mapper;

import com.example.cowmarketplace.dto.response.CowDetailsResponse;
import com.example.cowmarketplace.dto.response.CowImageResponse;
import com.example.cowmarketplace.dto.response.CowResponse;
import com.example.cowmarketplace.entity.Cow;
import com.example.cowmarketplace.entity.CowImage;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CowMapper {

    public CowResponse toCowResponse(Cow cow, List<CowImage> images) {
        String primaryImageUrl = (images == null || images.isEmpty())
                ? null
                : images.get(0).getImageUrl();

        return CowResponse.builder()
                .id(cow.getId())
                .name(cow.getName())
                .breed(cow.getBreed())
                .age(cow.getAge())
                .gender(cow.getGender())
                .price(cow.getPrice())
                .location(cow.getLocation())
                .status(cow.getStatus())
                .primaryImageUrl(primaryImageUrl)
                .build();
    }

    public CowDetailsResponse toCowDetailsResponse(Cow cow, List<CowImage> images) {
        List<CowImageResponse> imageResponses = (images == null)
                ? Collections.emptyList()
                : images.stream()
                .map(img -> CowImageResponse.builder()
                        .id(img.getId())
                        .imageUrl(img.getImageUrl())
                        .build())
                .collect(Collectors.toList());

        String whatsappNumber = cow.getFarmer().getWhatsappNumber();
        String mobileNumber = cow.getFarmer().getMobileNumber();

        String callLink = "tel:" + mobileNumber;
        String whatsappLink = (whatsappNumber != null && !whatsappNumber.isBlank())
                ? "https://wa.me/91" + whatsappNumber
                : null;

        return CowDetailsResponse.builder()
                .id(cow.getId())
                .name(cow.getName())
                .breed(cow.getBreed())
                .age(cow.getAge())
                .gender(cow.getGender())
                .price(cow.getPrice())
                .description(cow.getDescription())
                .milkPerDay(cow.getMilkPerDay())
                .lactationNumber(cow.getLactationNumber())
                .vaccinationStatus(cow.getVaccinationStatus())
                .healthStatus(cow.getHealthStatus())
                .location(cow.getLocation())
                .status(cow.getStatus())
                .createdAt(cow.getCreatedAt())
                .farmerName(cow.getFarmer().getFullName())
                .farmerMobile(mobileNumber)
                .farmerWhatsapp(whatsappNumber)
                .images(imageResponses)
                .callLink(callLink)
                .whatsappLink(whatsappLink)
                .build();
    }
}