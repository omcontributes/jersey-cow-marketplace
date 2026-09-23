package com.example.cowmarketplace.controller.service;

import com.example.cowmarketplace.dto.request.CowUpdateRequest;
import com.example.cowmarketplace.entity.*;
import com.example.cowmarketplace.exception.CowAlreadySoldException;
import com.example.cowmarketplace.exception.ForbiddenException;
import com.example.cowmarketplace.exception.ResourceNotFoundException;
import com.example.cowmarketplace.mapper.CowMapper;
import com.example.cowmarketplace.repository.CowImageRepository;
import com.example.cowmarketplace.repository.CowRepository;
import com.example.cowmarketplace.repository.UserRepository;
import com.example.cowmarketplace.service.FileStorageService;
import com.example.cowmarketplace.service.impl.CowServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CowServiceImplTest {

    @Mock
    private CowRepository cowRepository;

    @Mock
    private CowImageRepository cowImageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CowMapper cowMapper;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private CowServiceImpl cowService;

    private Cow cowOwnedByFarmerA;

    @BeforeEach
    void setUp() {
        User farmerA = User.builder().id(1L).fullName("Farmer A").role(Role.FARMER).build();

        cowOwnedByFarmerA = Cow.builder()
                .id(10L)
                .farmer(farmerA)
                .name("Ganga")
                .breed(Breed.JERSEY)
                .price(BigDecimal.valueOf(85000))
                .status(CowStatus.AVAILABLE)
                .build();
    }

    @Test
    void updateCow_byNonOwningFarmer_throwsForbiddenException() {
        Long farmerBId = 2L;

        when(cowRepository.findByIdAndFarmerId(10L, farmerBId)).thenReturn(Optional.empty());
        when(cowRepository.existsById(10L)).thenReturn(true); // cow exists, just not owned by Farmer B

        CowUpdateRequest request = new CowUpdateRequest();
        request.setPrice(BigDecimal.valueOf(50000));

        assertThatThrownBy(() -> cowService.updateCow(10L, request, farmerBId))
                .isInstanceOf(ForbiddenException.class);

        verify(cowRepository, never()).save(any(Cow.class));
    }

    @Test
    void updateCow_forNonExistentCow_throwsResourceNotFoundException() {
        Long farmerAId = 1L;

        when(cowRepository.findByIdAndFarmerId(999L, farmerAId)).thenReturn(Optional.empty());
        when(cowRepository.existsById(999L)).thenReturn(false);

        CowUpdateRequest request = new CowUpdateRequest();

        assertThatThrownBy(() -> cowService.updateCow(999L, request, farmerAId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateCow_byOwningFarmer_updatesSuccessfully() {
        Long farmerAId = 1L;

        when(cowRepository.findByIdAndFarmerId(10L, farmerAId)).thenReturn(Optional.of(cowOwnedByFarmerA));
        when(cowRepository.save(any(Cow.class))).thenReturn(cowOwnedByFarmerA);
        when(cowImageRepository.findByCowId(10L)).thenReturn(Collections.emptyList());

        CowUpdateRequest request = new CowUpdateRequest();
        request.setPrice(BigDecimal.valueOf(90000));

        cowService.updateCow(10L, request, farmerAId);

        verify(cowRepository, times(1)).save(any(Cow.class));
    }

    @Test
    void markAsSold_onAlreadySoldCow_throwsCowAlreadySoldException() {
        Long farmerAId = 1L;
        cowOwnedByFarmerA.setStatus(CowStatus.SOLD);

        when(cowRepository.findByIdAndFarmerId(10L, farmerAId)).thenReturn(Optional.of(cowOwnedByFarmerA));

        assertThatThrownBy(() -> cowService.markAsSold(10L, farmerAId))
                .isInstanceOf(CowAlreadySoldException.class);

        verify(cowRepository, never()).save(any(Cow.class));
    }

    @Test
    void markAsSold_onAvailableCow_marksSuccessfully() {
        Long farmerAId = 1L;

        when(cowRepository.findByIdAndFarmerId(10L, farmerAId)).thenReturn(Optional.of(cowOwnedByFarmerA));
        when(cowRepository.save(any(Cow.class))).thenReturn(cowOwnedByFarmerA);

        cowService.markAsSold(10L, farmerAId);

        verify(cowRepository, times(1)).save(any(Cow.class));
    }

    @Test
    void getPublicCowById_forSoldCow_throwsResourceNotFoundException() {
        cowOwnedByFarmerA.setStatus(CowStatus.SOLD);
        when(cowRepository.findById(10L)).thenReturn(Optional.of(cowOwnedByFarmerA));

        assertThatThrownBy(() -> cowService.getPublicCowById(10L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteCow_byOwningFarmer_deletesCowAndImages() {
        Long farmerAId = 1L;

        when(cowRepository.findByIdAndFarmerId(10L, farmerAId)).thenReturn(Optional.of(cowOwnedByFarmerA));
        when(cowImageRepository.findByCowId(10L)).thenReturn(Collections.emptyList());

        cowService.deleteCow(10L, farmerAId);

        verify(cowImageRepository, times(1)).deleteByCowId(10L);
        verify(cowRepository, times(1)).delete(cowOwnedByFarmerA);
    }
}