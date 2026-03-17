package com.atachakki.components.shop;

public record ShopOverviewResponseDto(
        Integer numberOfCustomer,
        Integer numberOfOrders,
        String totalCollectionAmount,
        String totalBalanceAmount
) {
}
