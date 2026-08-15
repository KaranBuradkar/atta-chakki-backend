package com.attachakki.exception.entityNotFound;

public class CustomerBelongToShopException extends EntityNotFoundException {
    public CustomerBelongToShopException(String message) {
        super(message, null);
    }

    public CustomerBelongToShopException(Long customerId, Long shopId) {
        super(String.format("Customer id %s not found in shop id %s", customerId, shopId), null);
    }
}
