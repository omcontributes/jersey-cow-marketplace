package com.example.cowmarketplace.controller;

import com.example.cowmarketplace.dto.request.CowCreateRequest;
import com.example.cowmarketplace.dto.request.CowUpdateRequest;
import com.example.cowmarketplace.dto.response.CowDetailsResponse;
import com.example.cowmarketplace.dto.response.CowResponse;
import com.example.cowmarketplace.security.CustomUserDetails;
import com.example.cowmarketplace.service.CowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/farmers/cows")
@RequiredArgsConstructor
@Tag(name = "Farmer - Cow Management", description = "Farmer-only endpoints to manage their own cow listings")
public class FarmerCowController {

    private final CowService cowService;

    @Operation(summary = "Create a new cow listing with images (minimum 3 required)")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CowResponse> createCow(
            @Valid @RequestPart("cow") CowCreateRequest request,
            @RequestPart("images") List<MultipartFile> images,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        CowResponse response = cowService.createCow(request, images, currentUser.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get all cows listed by the logged-in farmer")
    @GetMapping
    public ResponseEntity<List<CowResponse>> getMyCows(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(cowService.getMyCows(currentUser.getUserId()));
    }

    @Operation(summary = "Get one of the logged-in farmer's cows by id")
    @GetMapping("/{id}")
    public ResponseEntity<CowDetailsResponse> getMyCowById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(cowService.getMyCowById(id, currentUser.getUserId()));
    }

    @Operation(summary = "Update a cow listing (only the owning farmer can update)")
    @PutMapping("/{id}")
    public ResponseEntity<CowResponse> updateCow(
            @PathVariable Long id,
            @Valid @RequestBody CowUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(cowService.updateCow(id, request, currentUser.getUserId()));
    }

    @Operation(summary = "Delete a cow listing (only the owning farmer can delete)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCow(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        cowService.deleteCow(id, currentUser.getUserId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Mark a cow as SOLD (hides it from the public marketplace)")
    @PutMapping("/{id}/sold")
    public ResponseEntity<Void> markAsSold(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        cowService.markAsSold(id, currentUser.getUserId());
        return ResponseEntity.noContent().build();
    }
}