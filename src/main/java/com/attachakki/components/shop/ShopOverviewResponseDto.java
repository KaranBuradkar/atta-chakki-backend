package com.attachakki.components.shop;

public record ShopOverviewResponseDto(
        Integer numberOfCustomer,
        Integer numberOfOrders,
        String totalCollectionAmount,
        String totalBalanceAmount
) {
}
