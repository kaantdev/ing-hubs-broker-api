package com.ing.hubs.broker_api.service;

import com.ing.hubs.broker_api.dto.OrderRequestDTO;
import com.ing.hubs.broker_api.dto.OrderResponseDTO;

public interface OrderService {
    void matchOrder(Long orderId);
    OrderResponseDTO createOrder(OrderRequestDTO request);
    void cancelOrder(Long orderId);
}
