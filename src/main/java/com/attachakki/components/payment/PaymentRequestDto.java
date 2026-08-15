package com.attachakki.components.payment;

import com.attachakki.entity.type.PaymentStatus;
import com.attachakki.validation.PriceFormat;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequestDto(
        @PriceFormat()
        BigDecimal amount,
        @NotNull(message = "Payment mode is required") PaymentMode mode,
        @NotNull(message = "Payment status is required") PaymentStatus status,
        @NotNull(message = "Payment date is required") Long paymentDate
) {
        public PaymentRequestDto(BigDecimal amount, PaymentMode mode,
                                 PaymentStatus status, Long paymentDate) {
                this.amount = amount;
                this.mode = mode;
                this.status = status;
                this.paymentDate = paymentDate == null ? System.currentTimeMillis() : paymentDate;
        }
}
