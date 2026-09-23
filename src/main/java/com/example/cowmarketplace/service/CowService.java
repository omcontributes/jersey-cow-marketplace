package com.example.cowmarketplace.service;

import com.example.cowmarketplace.dto.request.CowCreateRequest;
import com.example.cowmarketplace.dto.request.CowSearchRequest;
import com.example.cowmarketplace.dto.request.CowUpdateRequest;
import com.example.cowmarketplace.dto.response.CowDetailsResponse;
import com.example.cowmarketplace.dto.response.CowResponse;
import com.example.cowmarketplace.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CowService {

    CowResponse createCow(CowCreateRequest request, List<MultipartFile> images, Long farmerId);

    List<CowResponse> getMyCows(Long farmerId);

    CowDetailsResponse getMyCowById(Long cowId, Long farmerId);

    CowResponse updateCow(Long cowId, CowUpdateRequest request, Long farmerId);

    void deleteCow(Long cowId, Long farmerId);

    void markAsSold(Long cowId, Long farmerId);

    CowDetailsResponse getPublicCowById(Long cowId);

    PagedResponse<CowResponse> searchCows(CowSearchRequest searchRequest, Pageable pageable);
}