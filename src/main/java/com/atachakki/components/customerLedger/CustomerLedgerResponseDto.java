package com.atachakki.components.customerLedger;

import java.math.BigDecimal;

public record CustomerLedgerResponseDto(
        Long id,
        Long customerId,
        BigDecimal amount,
        CustomerLedgerType type,
        CustomerLedgerStatus status,
        Long addedById,
        String addedByName,
        Long updatedById,
        String updatedByName,
        Long date,
        Long createdAt,
        Long updatedAt
) {
}
