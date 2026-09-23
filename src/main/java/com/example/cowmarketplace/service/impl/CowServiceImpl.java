package com.example.cowmarketplace.service.impl;

import com.example.cowmarketplace.dto.request.CowCreateRequest;
import com.example.cowmarketplace.dto.request.CowSearchRequest;
import com.example.cowmarketplace.dto.request.CowUpdateRequest;
import com.example.cowmarketplace.dto.response.CowDetailsResponse;
import com.example.cowmarketplace.dto.response.CowResponse;
import com.example.cowmarketplace.dto.response.PagedResponse;
import com.example.cowmarketplace.entity.Cow;
import com.example.cowmarketplace.entity.CowImage;
import com.example.cowmarketplace.entity.CowStatus;
import com.example.cowmarketplace.entity.User;
import com.example.cowmarketplace.exception.CowAlreadySoldException;
import com.example.cowmarketplace.exception.ForbiddenException;
import com.example.cowmarketplace.exception.InsufficientImagesException;
import com.example.cowmarketplace.exception.ResourceNotFoundException;
import com.example.cowmarketplace.mapper.CowMapper;
import com.example.cowmarketplace.repository.CowImageRepository;
import com.example.cowmarketplace.repository.CowRepository;
import com.example.cowmarketplace.repository.CowSpecification;
import com.example.cowmarketplace.repository.UserRepository;
import com.example.cowmarketplace.service.CowService;
import com.example.cowmarketplace.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CowServiceImpl implements CowService {

    private static final int MIN_IMAGES = 3;

    private final CowRepository cowRepository;
    private final CowImageRepository cowImageRepository;
    private final UserRepository userRepository;
    private final CowMapper cowMapper;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public CowResponse createCow(CowCreateRequest request, List<MultipartFile> images, Long farmerId) {

        if (images == null || images.size() < MIN_IMAGES) {
            throw new InsufficientImagesException("Minimum " + MIN_IMAGES + " images are required");
        }

        User farmer = userRepository.findById(farmerId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found"));

        Cow cow = Cow.builder()
                .farmer(farmer)
                .name(request.getName())
                .breed(request.getBreed())
                .age(request.getAge())
                .gender(request.getGender())
                .price(request.getPrice())
                .description(request.getDescription())
                .milkPerDay(request.getMilkPerDay())
                .lactationNumber(request.getLactationNumber())
                .vaccinationStatus(request.getVaccinationStatus())
                .healthStatus(request.getHealthStatus())
                .location(request.getLocation())
                .status(CowStatus.AVAILABLE)
                .build();

        Cow savedCow = cowRepository.save(cow);

        for (MultipartFile file : images) {
            String imageUrl = fileStorageService.store(file);
            CowImage cowImage = CowImage.builder()
                    .cow(savedCow)
                    .imageUrl(imageUrl)
                    .fileName(file.getOriginalFilename())
                    .build();
            cowImageRepository.save(cowImage);
        }

        List<CowImage> savedImages = cowImageRepository.findByCowId(savedCow.getId());
        return cowMapper.toCowResponse(savedCow, savedImages);
    }

    @Override
    public List<CowResponse> getMyCows(Long farmerId) {
        return cowRepository.findByFarmerId(farmerId).stream()
                .map(cow -> cowMapper.toCowResponse(cow, cowImageRepository.findByCowId(cow.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public CowDetailsResponse getMyCowById(Long cowId, Long farmerId) {
        Cow cow = findOwnedCowOrThrow(cowId, farmerId);
        List<CowImage> images = cowImageRepository.findByCowId(cow.getId());
        return cowMapper.toCowDetailsResponse(cow, images);
    }

    @Override
    @Transactional
    public CowResponse updateCow(Long cowId, CowUpdateRequest request, Long farmerId) {
        Cow cow = findOwnedCowOrThrow(cowId, farmerId);

        if (cow.getStatus() == CowStatus.SOLD) {
            throw new CowAlreadySoldException("Cannot edit a cow that has already been sold");
        }

        if (request.getName() != null) cow.setName(request.getName());
        if (request.getBreed() != null) cow.setBreed(request.getBreed());
        if (request.getAge() != null) cow.setAge(request.getAge());
        if (request.getGender() != null) cow.setGender(request.getGender());
        if (request.getPrice() != null) cow.setPrice(request.getPrice());
        if (request.getDescription() != null) cow.setDescription(request.getDescription());
        if (request.getMilkPerDay() != null) cow.setMilkPerDay(request.getMilkPerDay());
        if (request.getLactationNumber() != null) cow.setLactationNumber(request.getLactationNumber());
        if (request.getVaccinationStatus() != null) cow.setVaccinationStatus(request.getVaccinationStatus());
        if (request.getHealthStatus() != null) cow.setHealthStatus(request.getHealthStatus());
        if (request.getLocation() != null) cow.setLocation(request.getLocation());

        Cow updatedCow = cowRepository.save(cow);
        List<CowImage> images = cowImageRepository.findByCowId(updatedCow.getId());
        return cowMapper.toCowResponse(updatedCow, images);
    }

    @Override
    @Transactional
    public void deleteCow(Long cowId, Long farmerId) {
        Cow cow = findOwnedCowOrThrow(cowId, farmerId);

        List<CowImage> images = cowImageRepository.findByCowId(cow.getId());
        for (CowImage image : images) {
            fileStorageService.delete(image.getImageUrl());
        }
        cowImageRepository.deleteByCowId(cow.getId());
        cowRepository.delete(cow);
    }

    @Override
    @Transactional
    public void markAsSold(Long cowId, Long farmerId) {
        Cow cow = findOwnedCowOrThrow(cowId, farmerId);

        if (cow.getStatus() == CowStatus.SOLD) {
            throw new CowAlreadySoldException("This cow has already been marked as sold");
        }

        cow.setStatus(CowStatus.SOLD);
        cowRepository.save(cow);
    }

    @Override
    public CowDetailsResponse getPublicCowById(Long cowId) {
        Cow cow = cowRepository.findById(cowId)
                .filter(c -> c.getStatus() == CowStatus.AVAILABLE)
                .orElseThrow(() -> new ResourceNotFoundException("Cow not found"));
        List<CowImage> images = cowImageRepository.findByCowId(cow.getId());
        return cowMapper.toCowDetailsResponse(cow, images);
    }

    @Override
    public PagedResponse<CowResponse> searchCows(CowSearchRequest searchRequest, Pageable pageable) {

        Specification<Cow> spec = Specification
                .where(CowSpecification.hasStatus(CowStatus.AVAILABLE))
                .and(CowSpecification.hasBreed(searchRequest.getBreed()))
                .and(CowSpecification.hasCity(searchRequest.getCity()))
                .and(CowSpecification.priceGreaterThanOrEqual(searchRequest.getMinPrice()))
                .and(CowSpecification.priceLessThanOrEqual(searchRequest.getMaxPrice()))
                .and(CowSpecification.ageGreaterThanOrEqual(searchRequest.getMinAge()))
                .and(CowSpecification.ageLessThanOrEqual(searchRequest.getMaxAge()))
                .and(CowSpecification.milkPerDayGreaterThanOrEqual(searchRequest.getMinMilkPerDay()));

        Page<Cow> cowPage = cowRepository.findAll(spec, pageable);

        Page<CowResponse> responsePage = cowPage.map(cow ->
                cowMapper.toCowResponse(cow, cowImageRepository.findByCowId(cow.getId())));

        return PagedResponse.from(responsePage);
    }

    private Cow findOwnedCowOrThrow(Long cowId, Long farmerId) {
        return cowRepository.findByIdAndFarmerId(cowId, farmerId)
                .orElseThrow(() -> {
                    boolean cowExists = cowRepository.existsById(cowId);
                    if (cowExists) {
                        return new ForbiddenException("You do not have permission to access this cow");
                    }
                    return new ResourceNotFoundException("Cow not found");
                });
    }
}