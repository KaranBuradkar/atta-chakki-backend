package com.attachakki.components.order;

import com.attachakki.components.customerLedger.CustomerLedgerResponseDto;
import org.springframework.data.domain.Page;

public record OrderBundleResponseDto(
        Page<OrderResponseDto> orderPageResponseDto,
        Page<CustomerLedgerResponseDto> customerLedgerPageResponseDto
) {
}
