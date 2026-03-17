package com.atachakki.components.order;

import com.atachakki.entity.type.PaymentStatus;
import com.atachakki.entity.type.QuantityType;

import java.math.BigDecimal;

public record OrderResponseDto(
        Long id,
        Long orderItemId,
        Long customerId,
        String orderItemName,
        Integer quantity,
        QuantityType quantityType,
        BigDecimal totalAmount,
        PaymentStatus paymentStatus,
        Long addedById,
        String addedByName,
        Long updatedById,
        String updatedByName,
        Long orderDate,
        Long createdAt,
        Long updatedAt
) {}
