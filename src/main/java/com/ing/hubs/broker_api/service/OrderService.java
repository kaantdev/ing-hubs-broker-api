package com.ing.hubs.broker_api.service;

import com.ing.hubs.broker_api.dto.OrderRequestDTO;
import com.ing.hubs.broker_api.dto.OrderResponseDTO;
import com.ing.hubs.broker_api.enums.OrderSide;
import com.ing.hubs.broker_api.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    void matchOrder(Long orderId);
    OrderResponseDTO createOrder(OrderRequestDTO request);
    void cancelOrder(Long orderId);
    List<OrderResponseDTO> listOrders(Long customerId, LocalDateTime startDate, LocalDateTime endDate, Optional<OrderSide> side, Optional<OrderStatus> status);
    List<OrderResponseDTO> getPendingOrders();
}
