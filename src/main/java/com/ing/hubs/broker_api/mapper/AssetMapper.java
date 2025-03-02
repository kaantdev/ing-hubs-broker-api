package com.ing.hubs.broker_api.mapper;

import com.ing.hubs.broker_api.dto.AssetTransactionResponseDTO;
import com.ing.hubs.broker_api.entity.Asset;
import org.springframework.stereotype.Component;

@Component
public class AssetMapper {

    public AssetTransactionResponseDTO toResponse(Asset asset) {
        AssetTransactionResponseDTO response = new AssetTransactionResponseDTO();
        response.setCustomerId(asset.getCustomer().getId());
        response.setBalance(asset.getUsableSize());
        return response;
    }
}
