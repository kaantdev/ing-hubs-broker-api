package com.ing.hubs.broker_api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssetTransactionRequestDTO {
    private Long customerId;
    private double amount;
}
