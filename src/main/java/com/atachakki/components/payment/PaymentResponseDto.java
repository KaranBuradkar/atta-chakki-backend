package com.atachakki.components.payment;

import com.atachakki.entity.type.PaymentStatus;

import java.math.BigDecimal;

public record PaymentResponseDto (
    Long id,
    BigDecimal amount,
    PaymentMode mode,
    Long customerId,
    String customerName,
    Long receiverStaffId,
    String receiverName,
    PaymentStatus status,
    Long paymentDate,
    Long createdAt,
    Long updatedAt
) {
}

