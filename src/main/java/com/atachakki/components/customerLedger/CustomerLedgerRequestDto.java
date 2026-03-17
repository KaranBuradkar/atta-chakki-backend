package com.atachakki.components.customerLedger;

import com.atachakki.validation.PriceFormat;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CustomerLedgerRequestDto(
        @PriceFormat
        BigDecimal amount,
        @NotNull CustomerLedgerType type,
        @NotNull CustomerLedgerStatus status,
        @NotNull Long date
) {}
