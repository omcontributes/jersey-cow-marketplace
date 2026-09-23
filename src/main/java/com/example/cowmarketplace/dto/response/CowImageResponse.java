package com.example.cowmarketplace.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CowImageResponse {

    private Long id;
    private String imageUrl;
}
