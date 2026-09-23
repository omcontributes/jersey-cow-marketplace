package com.example.cowmarketplace.service;

import com.example.cowmarketplace.dto.request.ContactRequestDto;
import com.example.cowmarketplace.dto.response.ContactRequestResponse;

import java.util.List;

public interface ContactRequestService {

    ContactRequestResponse createContactRequest(ContactRequestDto request, Long buyerId);

    List<ContactRequestResponse> getContactRequestsForFarmer(Long farmerId);
}