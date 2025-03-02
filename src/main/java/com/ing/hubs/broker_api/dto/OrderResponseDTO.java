package com.ing.hubs.broker_api.dto;

import com.ing.hubs.broker_api.enums.OrderSide;
import com.ing.hubs.broker_api.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class OrderResponseDTO {
    private Long orderId;
    private String assetName;
    private OrderSide orderSide;
    private int size;
    private double price;
    private OrderStatus status;
    private LocalDateTime createDate;
}