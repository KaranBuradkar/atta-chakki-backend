package com.atachakki.components.customerLedger;

import com.atachakki.security.authorizationValidation.IsAdminOrShopOwnerOrShopkeeper;
import org.springframework.stereotype.Service;

@Service
public interface CustomerLedgerService {

    @IsAdminOrShopOwnerOrShopkeeper
    CustomerLedgerResponseDto create(Long shopId, Long customerId, CustomerLedgerRequestDto requestDto);

    CustomerLedgerResponseDto findCustomerLedger(Long shopId, Long customerId);
}
