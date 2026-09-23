package com.example.cowmarketplace.repository;

import com.example.cowmarketplace.entity.Cow;
import com.example.cowmarketplace.entity.CowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface CowRepository extends JpaRepository<Cow, Long>, JpaSpecificationExecutor<Cow> {

    Page<Cow> findByStatus(CowStatus status, Pageable pageable);

    List<Cow> findByFarmerId(Long farmerId);

    Optional<Cow> findByIdAndFarmerId(Long id, Long farmerId);
}