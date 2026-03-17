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
    Long createdAt,
    Long updatedAt
){
    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", amount=" + amount +
                ", mode=" + mode +
                ", customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", receiverStaffId=" + receiverStaffId +
                ", receiverName='" + receiverName + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

