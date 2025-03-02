package com.ing.hubs.broker_api.service.impl;

import com.ing.hubs.broker_api.dto.OrderRequestDTO;
import com.ing.hubs.broker_api.dto.OrderResponseDTO;
import com.ing.hubs.broker_api.entity.Asset;
import com.ing.hubs.broker_api.entity.Order;
import com.ing.hubs.broker_api.enums.OrderSide;
import com.ing.hubs.broker_api.enums.OrderStatus;
import com.ing.hubs.broker_api.exception.InsufficientBalanceException;
import com.ing.hubs.broker_api.mapper.OrderMapper;
import com.ing.hubs.broker_api.repository.OrderRepository;
import com.ing.hubs.broker_api.service.AssetService;
import com.ing.hubs.broker_api.service.OrderService;
import com.ing.hubs.broker_api.util.AuthorizationUtil;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final AssetService assetService;
    private final OrderRepository orderRepository;
    private final AuthorizationUtil authorizationUtil;
    private final OrderMapper orderMapper;
    private Set<String> validAssets;


    @Value("${app.valid-assets}")
    private String validAssetsProperty;

    @PostConstruct
    public void initializeValidAssets() {
        this.validAssets = new HashSet<>(Arrays.asList(validAssetsProperty.split(",")));
    }

    @Transactional
    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO request) {
        authorizationUtil.validateUserAccess(request.getCustomerId());
        validateRequest(request);
        Long customerId = request.getCustomerId();
        OrderSide side = OrderSide.valueOf(request.getSide());
        int size = request.getSize();
        double price = request.getPrice();

        String assetName = request.getAssetName().toUpperCase();
        Asset tryAsset = assetService.findOrCreateTryAsset(customerId);

        if (side == OrderSide.BUY) {
            handleBuyOrder(tryAsset, size, price);
        } else {
            Asset stockAsset = assetService.findOrCreateAsset(customerId, assetName); // asset stock only for sell orders
            handleSellOrder(stockAsset, size);
        }

        Order order = orderMapper.toEntity(request);
        order.setStatus(OrderStatus.PENDING);
        order.setCreateDate(LocalDateTime.now());

        orderRepository.save(order);
        return orderMapper.toResponse(order);
    }

    @Transactional
    @Override
    public void matchOrder(Long orderId) {
        Order order = getPendingOrder(orderId);
        updateCustomerAssets(order);
        order.setStatus(OrderStatus.MATCHED);
        orderRepository.save(order);
    }

    @Transactional
    @Override
    public void cancelOrder(Long orderId) {
        Order order = getPendingOrder(orderId);
        authorizationUtil.validateUserAccess(order.getCustomer().getId());
        if (order.getOrderSide() == OrderSide.BUY) {
            refundBuyOrder(order);
        } else {
            refundSellOrder(order);
        }

        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
    }

    @Transactional
    @Override
    public List<OrderResponseDTO> listOrders(Long customerId, LocalDateTime startDate, LocalDateTime endDate, Optional<OrderSide> side, Optional<OrderStatus> status) {
        authorizationUtil.validateUserAccess(customerId);
        return orderRepository.findByCustomerIdAndCreateDateBetween(customerId, startDate, endDate)
                .stream()
                .filter(order -> side.map(s -> s == order.getOrderSide()).orElse(true))
                .filter(order -> status.map(s -> s == order.getStatus()).orElse(true))
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponseDTO> getPendingOrders() {
        return orderRepository.findByStatus(OrderStatus.PENDING)
                .stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    private void validateRequest(OrderRequestDTO request) {
        if (request.getSize() <= 0 || request.getPrice() <= 0) {
            throw new IllegalArgumentException("Size and price must be greater than zero.");
        }
        if (!validAssets.contains(request.getAssetName().toUpperCase())) {
            throw new IllegalArgumentException("Invalid asset name: " + request.getAssetName());
        }
    }

    private Order getPendingOrder(Long orderId) {
        return orderRepository.findByIdAndStatus(orderId, OrderStatus.PENDING)
                .orElseThrow(() -> new RuntimeException("Order not found or already matched: " + orderId));
    }

    private void handleBuyOrder(Asset tryAsset, int size, double price) {
        double totalCost = size * price;
        if (tryAsset.getUsableSize() < totalCost) {
            throw new InsufficientBalanceException("Insufficient TRY balance. Available: " + tryAsset.getUsableSize());
        }
        tryAsset.setUsableSize(tryAsset.getUsableSize() - totalCost);
        assetService.saveAsset(tryAsset);
    }

    private void handleSellOrder(Asset stockAsset, int size) {
        if (stockAsset.getUsableSize() < size) {
            throw new InsufficientBalanceException("Insufficient " + stockAsset.getAssetName() + " balance. Available: " + stockAsset.getUsableSize());
        }
        stockAsset.setUsableSize(stockAsset.getUsableSize() - size);
        assetService.saveAsset(stockAsset);
    }

    private void refundBuyOrder(Order order) {
        Asset tryAsset = assetService.findOrCreateTryAsset(order.getCustomer().getId());
        double totalCost = order.getSize() * order.getPrice();
        tryAsset.setUsableSize(tryAsset.getUsableSize() + totalCost);
        assetService.saveAsset(tryAsset);
    }

    private void refundSellOrder(Order order) {
        Asset stockAsset = assetService.findOrCreateAsset(order.getCustomer().getId(), order.getAssetName());
        stockAsset.setUsableSize(stockAsset.getUsableSize() + order.getSize());
        assetService.saveAsset(stockAsset);
    }

    private void updateCustomerAssets(Order order) {
        Long customerId = order.getCustomer().getId();
        Asset tryAsset = assetService.findOrCreateTryAsset(customerId);
        Asset stockAsset = assetService.findOrCreateAsset(customerId, order.getAssetName());
        double totalAmount = order.getSize() * order.getPrice();

        if (order.getOrderSide() == OrderSide.BUY) {
            tryAsset.setSize(tryAsset.getSize() - totalAmount);
            stockAsset.setSize(stockAsset.getSize() + order.getSize());
            stockAsset.setUsableSize(stockAsset.getUsableSize() + order.getSize());
        } else {
            stockAsset.setSize(stockAsset.getSize() - order.getSize());
            tryAsset.setUsableSize(tryAsset.getUsableSize() + totalAmount);
            tryAsset.setSize(tryAsset.getSize() + totalAmount);
        }

        assetService.saveAsset(tryAsset);
        assetService.saveAsset(stockAsset);
    }
}