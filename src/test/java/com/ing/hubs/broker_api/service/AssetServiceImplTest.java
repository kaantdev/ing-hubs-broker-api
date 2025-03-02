package com.ing.hubs.broker_api.service;

import com.ing.hubs.broker_api.dto.AssetTransactionResponseDTO;
import com.ing.hubs.broker_api.entity.Asset;
import com.ing.hubs.broker_api.entity.Customer;
import com.ing.hubs.broker_api.exception.InsufficientBalanceException;
import com.ing.hubs.broker_api.mapper.AssetMapper;
import com.ing.hubs.broker_api.repository.AssetRepository;
import com.ing.hubs.broker_api.service.impl.AssetServiceImpl;
import com.ing.hubs.broker_api.util.AuthorizationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetServiceImplTest {

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private AssetMapper assetMapper;

    @Mock
    private AuthorizationUtil authorizationUtil;

    @InjectMocks
    private AssetServiceImpl assetService;

    private final Long customerId = 1L;
    private Asset existingAsset;

    @BeforeEach
    void setUp() {
        existingAsset = Asset.builder()
                .customer(Customer.builder().id(customerId).build())
                .assetName("TRY")
                .size(1000)
                .usableSize(1000)
                .build();
    }

    @Test
    void shouldDepositMoneySuccessfully() {
        double depositAmount = 500.0;
        when(assetRepository.findByCustomerId(customerId)).thenReturn(List.of(existingAsset));
        when(assetRepository.save(any(Asset.class))).thenReturn(existingAsset);
        when(assetMapper.toResponse(any(Asset.class))).thenReturn(new AssetTransactionResponseDTO());

        AssetTransactionResponseDTO response = assetService.depositMoney(customerId, depositAmount);

        assertNotNull(response);
        assertEquals(1500, existingAsset.getSize());
        assertEquals(1500, existingAsset.getUsableSize());
        verify(assetRepository, times(1)).save(any(Asset.class));
    }

    @Test
    void shouldWithdrawMoneySuccessfully() {
        double withdrawAmount = 200.0;
        when(assetRepository.findByCustomerId(customerId)).thenReturn(List.of(existingAsset));
        when(assetRepository.save(any(Asset.class))).thenReturn(existingAsset);
        when(assetMapper.toResponse(any(Asset.class))).thenReturn(new AssetTransactionResponseDTO());

        AssetTransactionResponseDTO response = assetService.withdrawMoney(customerId, withdrawAmount);

        assertNotNull(response);
        assertEquals(800, existingAsset.getSize());
        assertEquals(800, existingAsset.getUsableSize());
        verify(assetRepository, times(1)).save(any(Asset.class));
    }

    @Test
    void shouldThrowExceptionWhenInsufficientBalance() {
        double withdrawAmount = 2000.0;
        when(assetRepository.findByCustomerId(customerId)).thenReturn(List.of(existingAsset));

        assertThrows(InsufficientBalanceException.class, () -> assetService.withdrawMoney(customerId, withdrawAmount));
    }

    @Test
    void shouldFindOrCreateTryAssetWhenExists() {
        when(assetRepository.findByCustomerId(customerId)).thenReturn(List.of(existingAsset));

        Asset result = assetService.findOrCreateTryAsset(customerId);

        assertNotNull(result);
        assertEquals("TRY", result.getAssetName());
    }

    @Test
    void shouldCreateNewTryAssetWhenNotExists() {
        when(assetRepository.findByCustomerId(customerId)).thenReturn(List.of());
        when(assetRepository.save(any(Asset.class))).thenReturn(existingAsset);

        Asset result = assetService.findOrCreateTryAsset(customerId);

        assertNotNull(result);
        assertEquals("TRY", result.getAssetName());
        verify(assetRepository, times(1)).save(any(Asset.class));
    }
}
