package com.attachakki.components.order;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OrderMapper {

    Order toEntity(OrderRequestDto requestDto);

    @Mapping(source = "shopOrderItemPrice.id", target = "orderItemId")
    @Mapping(source = "shopOrderItemPrice.orderItem.name", target = "orderItemName")
    @Mapping(source = "shopOrderItemPrice.quantityType", target = "quantityType")
    @Mapping(source = "addedBy.id", target = "addedById")
    @Mapping(source = "addedBy.userDetail.name", target = "addedByName")
    @Mapping(source = "updatedBy.id", target = "updatedById")
    @Mapping(source = "updatedBy.userDetail.name", target = "updatedByName")
    @Mapping(source = "customer.id", target = "customerId")
    OrderResponseDto toResponseDto(Order order);
}
