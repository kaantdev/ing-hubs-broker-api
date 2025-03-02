package com.ing.hubs.broker_api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "asset")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private String assetName;

    @Column(nullable = false)
    private double size;

    @Column(nullable = false)
    private double usableSize;
}
