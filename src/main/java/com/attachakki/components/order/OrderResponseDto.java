package com.attachakki.components.order;

import com.attachakki.entity.type.PaymentStatus;
import com.attachakki.entity.type.QuantityType;

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
