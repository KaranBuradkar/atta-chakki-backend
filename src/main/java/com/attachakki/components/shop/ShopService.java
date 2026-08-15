package com.attachakki.components.shop;

import com.attachakki.security.authorizationValidation.IsAdminOrShopOwnerOrShopkeeper;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public interface ShopService {

    Page<ShopShortResponseDto> getAllShops(int page, int size, String direction, String sort);

    @PreAuthorize(value = "@permissionGuard.check(#shopId, 'SHOP', 'READ')")
    ShopResponseDto getShopDetails(Long shopId);

    @PreAuthorize(value = "hasAnyRole('ADMIN', 'CLIENT', 'MANAGEMENT')")
    ShopResponseDto create(ShopRequestDto requestDto);

    @IsAdminOrShopOwnerOrShopkeeper
    ShopResponseDto updateShopFields(Long shopId, ShopRequestDto requestDto);

    @IsAdminOrShopOwnerOrShopkeeper
    ShopResponseDto updateShopStatus(Long shopId, ShopStatus status);

    @PreAuthorize(value = "hasAnyRole('ADMIN') OR hasAuthority('SHOP_'+#shopId+'_OWNER')")
    void deleteShop(Long shopId);
}
