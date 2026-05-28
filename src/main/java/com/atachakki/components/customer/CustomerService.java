package com.atachakki.components.customer;

import com.atachakki.security.authorizationValidation.IsAdminOrShopOwnerOrShopkeeper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CustomerService {

    @PreAuthorize("@permissionGuard.check(#shopId, 'CUSTOMER', 'READ')")
    Page<CustomerResponseShortDto> findCustomers(
           Long shopId, Integer page, Integer size, String direction, String sort, String name);

    @PreAuthorize("@permissionGuard.check(#shopId, 'CUSTOMER', 'READ')")
    CustomerResponseDto findCustomer(Long shopId, Long customerId);

    @PreAuthorize("@permissionGuard.check(#shopId, 'CUSTOMER', 'READ')")
    List<Long> findCustomerIds(Long shopId);

    @PreAuthorize("@permissionGuard.isOwner(#shopId)")
    Page<CustomerResponseDto> findAllCustomers(
            Long shopId, Integer page, Integer size, String direction, String name);

    @PreAuthorize("@permissionGuard.check(#shopId, 'CUSTOMER', 'WRITE')")
    CustomerResponseDto create(Long shopId, CustomerRequestDto requestDto);

    @PreAuthorize("@permissionGuard.check(#shopId, 'CUSTOMER', 'WRITE')")
    List<CustomerResponseDto> createAll(Long shopId, @Valid List<CustomerRequestDto> requestDto);

    @PreAuthorize("@permissionGuard.check(#shopId, 'CUSTOMER', 'UPDATE')")
    CustomerResponseDto updateCustomerBlockStatus(Long shopId, Long customerId, Boolean block);

    @PreAuthorize("@permissionGuard.check(#shopId, 'CUSTOMER', 'UPDATE')")
    CustomerResponseDto updateCustomerFields(
            Long shopId, Long customerId, CustomerRequestDto requestDto);

    @IsAdminOrShopOwnerOrShopkeeper
    void deleteById(Long shopId, Long customerId);
}
