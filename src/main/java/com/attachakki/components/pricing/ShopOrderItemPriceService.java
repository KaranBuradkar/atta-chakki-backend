package com.attachakki.components.pricing;

import com.attachakki.security.authorizationValidation.IsAdminOrShopOwnerOrShopkeeper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public interface ShopOrderItemPriceService {

    @PreAuthorize(value = "@permissionGuard.check(#shopId, 'ORDER_ITEM_PRICE', 'READ')")
    Page<ShopOrderItemPriceResponseDto> getShopOrderItemsPrice(
            Long shopId, Integer page, Integer size, String direction, String sort);

    @IsAdminOrShopOwnerOrShopkeeper
    ShopOrderItemPriceResponseDto create(Long shopId, @Valid ShopOrderItemPriceRequestDto requestDto);

    @IsAdminOrShopOwnerOrShopkeeper
    ShopOrderItemPriceResponseDto update(Long shopId, Long orderItemPriceId, ShopOrderItemPriceRequestDto updateRequest);

    @IsAdminOrShopOwnerOrShopkeeper
    void delete(Long shopId, Long orderItemPriceId);
}
