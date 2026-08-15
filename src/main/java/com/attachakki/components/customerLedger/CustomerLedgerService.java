package com.attachakki.components.customerLedger;

import com.attachakki.security.authorizationValidation.IsAdminOrShopOwnerOrShopkeeper;
import org.springframework.stereotype.Service;

@Service
public interface CustomerLedgerService {

    @IsAdminOrShopOwnerOrShopkeeper
    CustomerLedgerResponseDto create(Long shopId, Long customerId, CustomerLedgerRequestDto requestDto);

    CustomerLedgerResponseDto findCustomerLedger(Long shopId, Long customerId);
}
