package com.ing.hubs.broker_api.service;

import com.ing.hubs.broker_api.dto.AssetTransactionResponseDTO;
import com.ing.hubs.broker_api.entity.Asset;
import com.ing.hubs.broker_api.entity.Customer;

import java.util.Optional;

public interface AssetService {
    AssetTransactionResponseDTO depositMoney(Long customerId, double amount);
    AssetTransactionResponseDTO withdrawMoney(Long customerId, double amount);
    void saveAsset(Asset asset);
    Asset findOrCreateAsset(Long customerId, String assetName);
    Asset findOrCreateTryAsset(Long customerId);
}