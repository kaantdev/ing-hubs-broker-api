package com.ing.hubs.broker_api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssetTransactionResponseDTO {
    private Long customerId;
    private String assetName = "TRY";
    private double balance;
}
