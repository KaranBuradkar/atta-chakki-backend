package com.attachakki.components.customerLedger;

import com.attachakki.validation.PriceFormat;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CustomerLedgerRequestDto(
        @PriceFormat
        BigDecimal amount,
        @NotNull CustomerLedgerType type,
        @NotNull CustomerLedgerStatus status,
        @NotNull Long date
) {}
