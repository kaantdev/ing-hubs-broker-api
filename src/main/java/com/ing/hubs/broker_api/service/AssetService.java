package com.ing.hubs.broker_api.service;

public interface AssetService {
    void depositMoney(Long customerId, int amount);
    void withdrawMoney(Long customerId, int amount);
}