package com.ing.hubs.broker_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssetTransactionResponseDTO {
    private Long customerId;
    private String assetName = "TRY";
    private double balance;
}
