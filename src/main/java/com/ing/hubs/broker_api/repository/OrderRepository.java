package com.ing.hubs.broker_api.repository;

import com.ing.hubs.broker_api.entity.Order;
import com.ing.hubs.broker_api.enums.OrderStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>{
    List<Order> findByCustomerIdAndCreateDateBetween(Long customerId, LocalDateTime startDate, LocalDateTime endDate);
    List<Order> findByStatus(OrderStatus status);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Order> findByIdAndStatus(Long orderId, OrderStatus status);
}
