package com.example.cowmarketplace.repository;

import com.example.cowmarketplace.entity.CowImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CowImageRepository extends JpaRepository<CowImage, Long> {

    List<CowImage> findByCowId(Long cowId);

    void deleteByCowId(Long cowId);
}
