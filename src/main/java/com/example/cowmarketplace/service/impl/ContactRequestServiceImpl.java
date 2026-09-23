package com.example.cowmarketplace.service.impl;

import com.example.cowmarketplace.dto.request.ContactRequestDto;
import com.example.cowmarketplace.dto.response.ContactRequestResponse;
import com.example.cowmarketplace.entity.ContactRequest;
import com.example.cowmarketplace.entity.Cow;
import com.example.cowmarketplace.entity.User;
import com.example.cowmarketplace.exception.ResourceNotFoundException;
import com.example.cowmarketplace.repository.ContactRequestRepository;
import com.example.cowmarketplace.repository.CowRepository;
import com.example.cowmarketplace.repository.UserRepository;
import com.example.cowmarketplace.service.ContactRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactRequestServiceImpl implements ContactRequestService {

    private final ContactRequestRepository contactRequestRepository;
    private final CowRepository cowRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ContactRequestResponse createContactRequest(ContactRequestDto request, Long buyerId) {

        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found"));

        Cow cow = cowRepository.findById(request.getCowId())
                .orElseThrow(() -> new ResourceNotFoundException("Cow not found"));

        ContactRequest contactRequest = ContactRequest.builder()
                .buyer(buyer)
                .farmer(cow.getFarmer())
                .cow(cow)
                .message(request.getMessage())
                .build();

        ContactRequest saved = contactRequestRepository.save(contactRequest);

        return toResponse(saved);
    }

    @Override
    public List<ContactRequestResponse> getContactRequestsForFarmer(Long farmerId) {
        return contactRequestRepository.findByFarmerIdOrderByCreatedAtDesc(farmerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ContactRequestResponse toResponse(ContactRequest cr) {
        return ContactRequestResponse.builder()
                .id(cr.getId())
                .cowId(cr.getCow().getId())
                .cowName(cr.getCow().getName())
                .buyerName(cr.getBuyer().getFullName())
                .buyerMobile(cr.getBuyer().getMobileNumber())
                .message(cr.getMessage())
                .createdAt(cr.getCreatedAt())
                .build();
    }
}