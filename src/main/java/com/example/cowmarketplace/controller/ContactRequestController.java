package com.example.cowmarketplace.controller;

import com.example.cowmarketplace.dto.request.ContactRequestDto;
import com.example.cowmarketplace.dto.response.ContactRequestResponse;
import com.example.cowmarketplace.security.CustomUserDetails;
import com.example.cowmarketplace.service.ContactRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Contact Requests", description = "Buyer-to-farmer contact messages about a specific cow")
public class ContactRequestController {

    private final ContactRequestService contactRequestService;

    @Operation(summary = "Send a contact request to a farmer about one of their cows")
    @PostMapping("/api/contact-requests")
    public ResponseEntity<ContactRequestResponse> createContactRequest(
            @Valid @RequestBody ContactRequestDto request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        ContactRequestResponse response =
                contactRequestService.createContactRequest(request, currentUser.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get all contact requests received by the logged-in farmer")
    @GetMapping("/api/farmers/contact-requests")
    public ResponseEntity<List<ContactRequestResponse>> getMyContactRequests(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(contactRequestService.getContactRequestsForFarmer(currentUser.getUserId()));
    }
}