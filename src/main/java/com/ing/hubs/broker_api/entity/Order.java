package com.ing.hubs.broker_api.entity;

import com.ing.hubs.broker_api.enums.OrderSide;
import com.ing.hubs.broker_api.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // The unique identifier of the order

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer; // The customer who placed the order

    @Column(nullable = false)
    private String assetName; // The name of the asset

    @Enumerated(EnumType.STRING)
    private OrderSide orderSide; // BUY or SELL

    @Column(nullable = false)
    private int size; // The number of units of the asset

    @Column(nullable = false)
    private double price; // The price per unit of the asset

    @Enumerated(EnumType.STRING)
    private OrderStatus status; //PENDING, MATCHED, CANCELED

    @Column(nullable = false)
    private LocalDateTime createDate; // The date and time the order was created
}
