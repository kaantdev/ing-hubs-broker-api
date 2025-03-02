package com.ing.hubs.broker_api.repository;

import com.ing.hubs.broker_api.entity.Asset;
import com.ing.hubs.broker_api.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    List<Asset> findByCustomerId(Long customerId);
}
