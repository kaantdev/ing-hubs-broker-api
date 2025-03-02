package com.ing.hubs.broker_api.service.impl;

import com.ing.hubs.broker_api.dto.AssetTransactionResponseDTO;
import com.ing.hubs.broker_api.entity.Asset;
import com.ing.hubs.broker_api.entity.Customer;
import com.ing.hubs.broker_api.exception.InsufficientBalanceException;
import com.ing.hubs.broker_api.mapper.AssetMapper;
import com.ing.hubs.broker_api.repository.AssetRepository;
import com.ing.hubs.broker_api.service.AssetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;
    private final AssetMapper assetMapper;

    @Override
    public AssetTransactionResponseDTO depositMoney(Long customerId, double amount) {
        Asset tryBalance = findOrCreateTryAsset(customerId);

        tryBalance.setSize(tryBalance.getSize() + amount);
        tryBalance.setUsableSize(tryBalance.getUsableSize() + amount);
        return assetMapper.toResponse(assetRepository.save(tryBalance));
    }

    @Override
    public AssetTransactionResponseDTO withdrawMoney(Long customerId, double amount) {
        Asset tryBalance = findOrCreateTryAsset(customerId);

        if (tryBalance.getUsableSize() < amount) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        tryBalance.setSize(tryBalance.getSize() - amount);
        tryBalance.setUsableSize(tryBalance.getUsableSize() - amount);
        return assetMapper.toResponse(assetRepository.save(tryBalance));
    }

    @Override
    public void saveAsset(Asset asset) {
        assetRepository.save(asset);
    }

    @Override
    public Asset findOrCreateAsset(Long customerId, String assetName) {
        return assetRepository.findByCustomerId(customerId).stream()
                .filter(asset -> asset.getAssetName().equals(assetName))
                .findFirst()
                .orElseGet(() -> createNewAsset(customerId, assetName));
    }

    private Asset createNewAsset(Long customerId, String assetName) {
        Asset newAsset = Asset.builder()
                .customer(Customer.builder().id(customerId).build())
                .assetName(assetName)
                .size(0) // Initial size is 0
                .usableSize(0)
                .build();
        return assetRepository.save(newAsset);
    }

    @Override
    public Asset findOrCreateTryAsset(Long customerId) {
        return assetRepository.findByCustomerId(customerId).stream()
                .filter(asset -> asset.getAssetName().equals("TRY"))
                .findFirst()
                .orElseGet(() -> createNewAsset(customerId, "TRY"));
    }
}
