package com.attachakki.components.pricing;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ShopOrderItemPriceMapper {

    ShopOrderItemPrice toEntity(ShopOrderItemPriceRequestDto requestDto);

    @Mapping(source = "orderItem.id", target = "orderItemId")
    @Mapping(source = "orderItem.name", target = "orderItemName")
    ShopOrderItemPriceResponseDto toResponseDto(ShopOrderItemPrice shopOrderItemPrice);
}
