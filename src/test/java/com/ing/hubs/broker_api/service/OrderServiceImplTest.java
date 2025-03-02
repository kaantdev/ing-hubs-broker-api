package com.ing.hubs.broker_api.service;

import com.ing.hubs.broker_api.dto.OrderRequestDTO;
import com.ing.hubs.broker_api.dto.OrderResponseDTO;
import com.ing.hubs.broker_api.entity.Asset;
import com.ing.hubs.broker_api.entity.Customer;
import com.ing.hubs.broker_api.entity.Order;
import com.ing.hubs.broker_api.enums.OrderSide;
import com.ing.hubs.broker_api.enums.OrderStatus;
import com.ing.hubs.broker_api.exception.InsufficientBalanceException;
import com.ing.hubs.broker_api.mapper.OrderMapper;
import com.ing.hubs.broker_api.repository.OrderRepository;
import com.ing.hubs.broker_api.service.impl.OrderServiceImpl;
import com.ing.hubs.broker_api.util.AuthorizationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private AssetService assetService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AuthorizationUtil authorizationUtil;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderRequestDTO orderRequest;
    private Order order;
    private Asset tryAsset;
    private Asset stockAsset;
    private Customer customer;
    private Set<String> validAssets;

    @BeforeEach
    void setUp() {
        customer = Customer.builder().id(1L).build();

        orderRequest = new OrderRequestDTO(1L, "BTC", "BUY", 10, 50000);

        order = Order.builder()
                .id(1L)
                .customer(customer)
                .assetName("BTC")
                .orderSide(OrderSide.BUY)
                .size(10)
                .price(50000)
                .status(OrderStatus.PENDING)
                .createDate(LocalDateTime.now())
                .build();

        tryAsset = Asset.builder()
                .customer(customer)
                .assetName("TRY")
                .size(1_000_000)
                .usableSize(1_000_000)
                .build();

        stockAsset = Asset.builder()
                .customer(customer)
                .assetName("BTC")
                .size(50)
                .usableSize(50)
                .build();

        validAssets = new HashSet<>();
        validAssets.add("BTC");
        validAssets.add("ETH");
        validAssets.add("TRY");

        orderService = spy(new OrderServiceImpl(assetService, orderRepository, authorizationUtil, orderMapper));
        ReflectionTestUtils.setField(orderService, "validAssetsProperty", "BTC,ETH,TRY");
        orderService.initializeValidAssets();
    }

    @Test
    void createOrder_shouldCreateBuyOrderSuccessfully() {
        OrderRequestDTO request = new OrderRequestDTO(1L, "BTC", "BUY", 10, 50000.00);
        Asset tryAsset = new Asset();
        tryAsset.setSize(1000000);
        tryAsset.setUsableSize(1000000);
        when(assetService.findOrCreateTryAsset(1L)).thenReturn(tryAsset);
        when(orderMapper.toEntity(request)).thenReturn(new Order());
        when(orderRepository.save(any(Order.class))).thenReturn(new Order());
        when(orderMapper.toResponse(any(Order.class))).thenReturn(new OrderResponseDTO());

        OrderResponseDTO response = orderService.createOrder(request);

        assertNotNull(response);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_shouldCreateSellOrderSuccessfully() {
        OrderRequestDTO request = new OrderRequestDTO(1L, "BTC", "SELL", 10, 50000.00);
        Asset stockAsset = new Asset();
        stockAsset.setSize(50);
        stockAsset.setUsableSize(50);
        when(assetService.findOrCreateAsset(1L, "BTC")).thenReturn(stockAsset);
        when(orderMapper.toEntity(request)).thenReturn(new Order());
        when(orderRepository.save(any(Order.class))).thenReturn(new Order());
        when(orderMapper.toResponse(any(Order.class))).thenReturn(new OrderResponseDTO());

        OrderResponseDTO response = orderService.createOrder(request);

        assertNotNull(response);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_shouldThrowExceptionWhenInvalidAsset() {
        OrderRequestDTO request = new OrderRequestDTO(1L, "INVALID", "BUY", 10, 50000.00);

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(request));
    }

    @Test
    void createOrder_shouldThrowExceptionWhenSizeIsZero() {
        OrderRequestDTO request = new OrderRequestDTO(1L, "BTC", "BUY", 0, 50000.00);

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(request));
    }

    @Test
    void createOrder_shouldThrowExceptionWhenPriceIsZero() {
        OrderRequestDTO request = new OrderRequestDTO(1L, "BTC", "BUY", 10, 0.00);

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(request));
    }
}