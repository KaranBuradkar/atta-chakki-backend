package com.atachakki.components.customer;

import com.atachakki.components.customerLedger.CustomerLedgerType;

public record CustomerResponseShortDto(
        Long id,
        String name,
        Boolean block,
        String specification,
        CustomerLedgerType type,
        String balance,
        Long updatedAt
) {}
