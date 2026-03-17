package com.atachakki.components.order;

import com.atachakki.components.customerLedger.CustomerLedgerResponseDto;
import org.springframework.data.domain.Page;

public record OrderBundleResponseDto(
        Page<OrderResponseDto> orderPageResponseDto,
        Page<CustomerLedgerResponseDto> customerLedgerPageResponseDto
) {
}
