package com.attachakki.security;

import com.attachakki.components.shop.Shop;
import com.attachakki.components.shop.ShopStatus;
import com.attachakki.exception.businessLogic.ShopClosedException;
import com.attachakki.exception.entityNotFound.ShopIdNotFoundException;
import com.attachakki.components.shop.ShopRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    private static final Logger log = LoggerFactory.getLogger(SecurityService.class);
    private final ShopRepository shopRepository;

    public SecurityService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    public boolean isShopActive(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> {
                    log.warn("shop id not found");
                    return new ShopIdNotFoundException(shopId);
                });
        if (shop.getStatus().equals(ShopStatus.CLOSED) || shop.getStatus().equals(ShopStatus.SUSPENDED)) {
            log.warn("shop is closed or deactivated");
            throw new ShopClosedException("shop is closed or deactivated");
        }
        if (shop.getStatus().equals(ShopStatus.MAINTENANCE)) {
            log.warn("shop under {}", ShopStatus.MAINTENANCE.name());
            throw new ShopClosedException("shop under "+ShopStatus.MAINTENANCE.name());
        }
        return shop.getStatus().equals(ShopStatus.ACTIVE);
    }
}
