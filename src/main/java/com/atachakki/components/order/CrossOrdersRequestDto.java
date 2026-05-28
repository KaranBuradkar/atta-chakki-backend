package com.atachakki.components.order;

import java.util.List;

public record CrossOrdersRequestDto(
        List<Long> orderIds,
        String totalAmount
) {
}
