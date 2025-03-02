package com.ing.hubs.broker_api.dto;

import com.ing.hubs.broker_api.enums.OrderSide;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDTO implements Serializable {
    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotBlank(message = "Asset name is required")
    @Size(min = 2, max = 10, message = "Asset name must be between 2 and 10 characters")
    private String assetName;


    @Pattern(regexp = "BUY|SELL", message = "Invalid order side")
    @NotNull(message = "Order side (BUY or SELL) is required")
    private String side;

    @Min(value = 1, message = "Size must be at least 1")
    private int size;

    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    private double price;
}