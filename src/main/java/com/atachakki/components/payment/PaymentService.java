package com.atachakki.components.payment;

import com.atachakki.security.authorizationValidation.IsAdminOrShopOwnerOrShopkeeper;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public interface PaymentService {

    @PreAuthorize("@permissionGuard.check(authentication, #shopId, 'PAYMENT', 'READ')")
    Page<PaymentResponseDto> findPayments(
            Long shopId, Long customerId, Integer page, Integer size, String direction, String sort);

    @PreAuthorize("@permissionGuard.check(authentication, #shopId, 'PAYMENT', 'READ')")
    PaymentResponseDto findPayment(Long shopId, Long paymentId);

    @PreAuthorize("@permissionGuard.check(authentication, #shopId, 'PAYMENT', 'WRITE')")
    PaymentResponseDto create(Long shopId, Long customerId, PaymentBundleRequestDto requestDto);

    @PreAuthorize("@permissionGuard.check(authentication, #shopId, 'PAYMENT', 'WRITE')")
    PaymentResponseDto createByAmount(Long shopId, Long customerId, PaymentRequestDto requestDto);

    @IsAdminOrShopOwnerOrShopkeeper
    PaymentResponseDto update(Long shopId, Long paymentId, PaymentRequestDto paymentRequestDto);

    @IsAdminOrShopOwnerOrShopkeeper
    void deleteById(Long shopId, Long paymentId);
}
