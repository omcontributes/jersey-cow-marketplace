package com.example.cowmarketplace.repository;

import com.example.cowmarketplace.entity.ContactRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactRequestRepository extends JpaRepository<ContactRequest, Long> {

    List<ContactRequest> findByFarmerIdOrderByCreatedAtDesc(Long farmerId);
}