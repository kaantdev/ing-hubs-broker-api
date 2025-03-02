package com.ing.hubs.broker_api.service.impl;

import com.ing.hubs.broker_api.dto.OrderRequestDTO;
import com.ing.hubs.broker_api.dto.OrderResponseDTO;
import com.ing.hubs.broker_api.entity.Asset;
import com.ing.hubs.broker_api.entity.Order;
import com.ing.hubs.broker_api.enums.OrderSide;
import com.ing.hubs.broker_api.enums.OrderStatus;
import com.ing.hubs.broker_api.mapper.OrderMapper;
import com.ing.hubs.broker_api.repository.AssetRepository;
import com.ing.hubs.broker_api.repository.OrderRepository;
import com.ing.hubs.broker_api.service.OrderService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final AssetRepository assetRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private Set<String> validAssets;

    @Value("${app.valid-assets}")
    private String validAssetsProperty;

    @PostConstruct
    private void initializeValidAssets() {
        this.validAssets = new HashSet<>(Arrays.asList(validAssetsProperty.split(",")));
    }


    @Transactional
    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO request) {
        Long customerId = request.getCustomerId();
        String assetName = request.getAssetName();
        OrderSide side = OrderSide.valueOf(request.getSide());
        int size = request.getSize();
        double price = request.getPrice();

        if (size <= 0 || price <= 0) {
            throw new IllegalArgumentException("Size and price must be greater than zero.");
        }

        if (!validAssets.contains(request.getAssetName().toUpperCase())) {
            throw new IllegalArgumentException("Invalid asset name: " + request.getAssetName());
        }

        if (side == OrderSide.BUY) {
            Asset tryBalance = assetRepository.findByCustomerIdAndAssetName(customerId, "TRY")
                    .orElseThrow(() -> new RuntimeException("TRY balance not found for customer: " + customerId));

            double totalCost = size * price;
            if (tryBalance.getUsableSize() < totalCost) {
                throw new RuntimeException("Insufficient TRY balance. Available: " + tryBalance.getUsableSize());
            }

            tryBalance.setUsableSize(tryBalance.getUsableSize() - totalCost);
            assetRepository.save(tryBalance);
        }
        if (side == OrderSide.SELL) {
            Asset asset = assetRepository.findByCustomerIdAndAssetName(customerId, assetName)
                    .orElseThrow(() -> new RuntimeException("Asset not found: " + assetName));

            if (asset.getUsableSize() < size) {
                throw new RuntimeException("Not enough shares to sell. Available: " + asset.getUsableSize());
            }
            asset.setUsableSize(asset.getUsableSize() - size);
            assetRepository.save(asset);
        }
        Order order = orderMapper.toEntity(request);
        order.setStatus(OrderStatus.PENDING);
        order.setCreateDate(LocalDateTime.now());

        orderRepository.save(order);
        return orderMapper.toResponse(order);
    }


    @Override
    public void matchOrder(Long orderId) {

    }

    @Transactional
    @Override
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        if (!OrderStatus.PENDING.equals(order.getStatus())) {
            throw new RuntimeException("Only PENDING orders can be canceled.");
        }

        if (order.getOrderSide() == OrderSide.BUY) {
            cancelBuyOrder(order);
        } else {
            cancelSellOrder(order);
        }

        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
    }

    private void cancelBuyOrder(Order order) {
        double totalCost = order.getSize() * order.getPrice();
        Long customerId = order.getCustomer().getId();

        Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(customerId, "TRY")
                .orElseThrow(() -> new RuntimeException("TRY balance not found for customer: " + customerId));

        tryAsset.setUsableSize(tryAsset.getUsableSize() + totalCost);
        assetRepository.save(tryAsset);
    }

    private void cancelSellOrder(Order order) {
        Long customerId = order.getCustomer().getId();

        Asset asset = assetRepository.findByCustomerIdAndAssetName(customerId, order.getAssetName())
                .orElseThrow(() -> new RuntimeException("Asset not found: " + order.getAssetName()));

        asset.setUsableSize(asset.getUsableSize() + order.getSize());
        assetRepository.save(asset);
    }
}
