package com.ing.hubs.broker_api.mapper;

import com.ing.hubs.broker_api.dto.OrderRequestDTO;
import com.ing.hubs.broker_api.dto.OrderResponseDTO;
import com.ing.hubs.broker_api.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(source = "customerId", target = "customer.id")
    @Mapping(source = "side", target = "orderSide")
    Order toEntity(OrderRequestDTO request);

    @Mapping(source = "id", target = "orderId")
    @Mapping(source = "orderSide", target = "orderSide")
    OrderResponseDTO toResponse(Order order);
}
