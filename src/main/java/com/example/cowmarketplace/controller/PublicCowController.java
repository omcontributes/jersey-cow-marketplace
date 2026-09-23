package com.example.cowmarketplace.controller;

import com.example.cowmarketplace.dto.request.CowSearchRequest;
import com.example.cowmarketplace.dto.response.CowDetailsResponse;
import com.example.cowmarketplace.dto.response.CowResponse;
import com.example.cowmarketplace.dto.response.PagedResponse;
import com.example.cowmarketplace.service.CowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cows")
@RequiredArgsConstructor
public class PublicCowController {

    private final CowService cowService;

    @GetMapping
    public ResponseEntity<PagedResponse<CowResponse>> searchCows(
            CowSearchRequest searchRequest,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(cowService.searchCows(searchRequest, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CowDetailsResponse> getCowById(@PathVariable Long id) {
        return ResponseEntity.ok(cowService.getPublicCowById(id));
    }
}