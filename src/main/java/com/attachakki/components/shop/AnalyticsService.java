package com.attachakki.components.shop;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public interface AnalyticsService {
    @PreAuthorize(value = "@permissionGuard.check(#shopId, 'SHOP', 'READ')")
    ShopOverviewResponseDto getShopOverview(Long shopId);
}
