package com.ing.hubs.broker_api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AssetTransactionRequestDTO {
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    private double amount;
}
