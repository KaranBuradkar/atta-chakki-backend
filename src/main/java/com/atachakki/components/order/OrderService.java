package com.atachakki.components.order;

import com.atachakki.security.authorizationValidation.IsAdminOrShopOwnerOrShopkeeper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public interface OrderService {

    @PreAuthorize("@permissionGuard.check(#shopId, 'ORDER', 'READ')")
    Page<OrderResponseDto> findOrders(
            Long shopId, Long customerId, Integer page,
            Integer size, String direction, String sort);

    @PreAuthorize("@permissionGuard.check(#shopId, 'ORDER', 'READ')")
    List<Long> findOrderIds(Long shopId, Long customerId);

    @PreAuthorize("@permissionGuard.check(#shopId, 'ORDER', 'READ')")
    String findTotalCustomerBalance(Long shopId, Long customerId);

    @PreAuthorize("@permissionGuard.check(#shopId, 'ORDER', 'WRITE')")
    OrderResponseDto createOrder(Long shopId, Long customerId, @Valid OrderRequestDto requestDto);

    @PreAuthorize("@permissionGuard.check(#shopId, 'ORDER', 'WRITE')")
    List<OrderResponseDto> createAllOrders(Long shopId, Long customerId, List<OrderRequestDto> orderRequests);

    @PreAuthorize("@permissionGuard.check(#shopId, 'ORDER', 'UPDATE')")
    OrderResponseDto updateOrderFields(Long shopId, Long orderId, OrderRequestDto requestDto);

    @IsAdminOrShopOwnerOrShopkeeper
    void deleteOrder(Long shopId, Long orderId);

    @IsAdminOrShopOwnerOrShopkeeper
    BigDecimal findTotalDebt(Long shopId);

    @PreAuthorize("@permissionGuard.check(#shopId, 'ORDER', 'UPDATE')")
    List<OrderResponseDto> crossOrders(
            Long shopId, Long customerId, @Valid CrossOrdersRequestDto request);
}
