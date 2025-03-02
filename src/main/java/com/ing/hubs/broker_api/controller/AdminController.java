package com.ing.hubs.broker_api.controller;


import com.ing.hubs.broker_api.dto.OrderResponseDTO;
import com.ing.hubs.broker_api.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/orders")
@Slf4j
@RequiredArgsConstructor
public class AdminController {

    private final OrderService orderService;

    @PatchMapping("/{orderId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> matchOrder(@PathVariable Long orderId) {
        orderService.matchOrder(orderId);
        return ResponseEntity.ok("Order successfully matched.");
    }

    @GetMapping("/pending-orders")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<OrderResponseDTO>> getPendingOrders() {
        List<OrderResponseDTO> pendingOrders = orderService.getPendingOrders();
        return ResponseEntity.ok(pendingOrders);
    }

}
