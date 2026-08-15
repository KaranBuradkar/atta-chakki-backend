package com.attachakki.exception.entityNotFound;

public class ShopIdNotFoundException extends EntityNotFoundException {
    public ShopIdNotFoundException(Long shopId) {
        this("shop not found with shop id "+shopId, null);
    }

    public ShopIdNotFoundException(String message, Object data) {
        super(message, data);
    }
}
