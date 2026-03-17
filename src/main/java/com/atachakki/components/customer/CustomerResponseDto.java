package com.atachakki.components.customer;

import com.atachakki.components.customerLedger.CustomerLedgerType;

public record CustomerResponseDto (
    Long id,
    String name,
    String email,
    String specification,
    Boolean block,
    CustomerLedgerType type,
    String balance,
    Long createdAt,
    Long updatedAt
) {}
