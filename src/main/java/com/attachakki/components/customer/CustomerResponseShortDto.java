package com.attachakki.components.customer;

import com.attachakki.components.customerLedger.CustomerLedgerType;

public record CustomerResponseShortDto(
        Long id,
        String name,
        Boolean block,
        String specification,
        CustomerLedgerType type,
        String balance,
        Long updatedAt
) {}