package com.attachakki.components.orderItem;

import com.attachakki.security.authorizationValidation.IsAdminOrShopOwnerOrShopkeeper;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;

public interface OrderItemService {

    @PreAuthorize("hasAnyAuthority('SHOP_'+#shopId+'_OWNER') OR " +
            "@permissionGuard.check(authentication, #shopId, 'ORDER_ITEM', 'READ')")
    Page<OrderItemResponseDto> findOrderItems(Integer page, Integer size, String direction, String sort);

    @IsAdminOrShopOwnerOrShopkeeper
    OrderItemResponseDto create(OrderItemRequestDto requestDto);

    @IsAdminOrShopOwnerOrShopkeeper
    OrderItemResponseDto update(Long orderItemId, OrderItemRequestDto requestDto);

    @IsAdminOrShopOwnerOrShopkeeper
    void deleteById(Long orderItemId);
}
