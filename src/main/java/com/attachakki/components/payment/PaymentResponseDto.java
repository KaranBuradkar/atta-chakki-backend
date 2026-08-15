package com.attachakki.components.payment;

import com.attachakki.entity.type.PaymentStatus;

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

