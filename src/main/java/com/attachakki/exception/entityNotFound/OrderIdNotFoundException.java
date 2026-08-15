package com.attachakki.exception.entityNotFound;

public class OrderIdNotFoundException extends EntityNotFoundException {
    public OrderIdNotFoundException(Long orderId) {
        super("Order not found with id "+orderId, null);
    }
}
