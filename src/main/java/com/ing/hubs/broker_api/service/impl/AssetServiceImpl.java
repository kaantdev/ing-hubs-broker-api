package com.ing.hubs.broker_api.service.impl;

import com.ing.hubs.broker_api.entity.Asset;
import com.ing.hubs.broker_api.entity.Customer;
import com.ing.hubs.broker_api.exception.InsufficientBalanceException;
import com.ing.hubs.broker_api.repository.AssetRepository;
import com.ing.hubs.broker_api.service.AssetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;

    @Override
    public void depositMoney(Long customerId, int amount) {
        Asset tryBalance = assetRepository.findByCustomerIdAndAssetName(customerId, "TRY")
                .orElseGet(() -> initializeTRYBalance(customerId));

        tryBalance.setSize(tryBalance.getSize() + amount);
        tryBalance.setUsableSize(tryBalance.getUsableSize() + amount);
        assetRepository.save(tryBalance);
    }

    @Override
    public void withdrawMoney(Long customerId, int amount) {
        Asset tryBalance = assetRepository.findByCustomerIdAndAssetName(customerId, "TRY")
                .orElseThrow(() -> new RuntimeException("Insufficient balance"));

        if (tryBalance.getUsableSize() < amount) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        tryBalance.setSize(tryBalance.getSize() - amount);
        tryBalance.setUsableSize(tryBalance.getUsableSize() - amount);
        assetRepository.save(tryBalance);
    }

    private Asset initializeTRYBalance(Long customerId) {
        return Asset.builder()
                .customer(Customer.builder().id(customerId).build())
                .assetName("TRY")
                .size(0)
                .usableSize(0)
                .build();
    }

}
