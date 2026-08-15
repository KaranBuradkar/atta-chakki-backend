package com.attachakki.exception.entityNotFound;

public class OrderItemNameNotFoundException extends EntityNotFoundException {
    public OrderItemNameNotFoundException(String orderItemName) {
        super(orderItemName, null);
    }

    public OrderItemNameNotFoundException(Long orderItemPriceId) {
        super("ShopOrderItemPriceId not found", null);
    }
}
