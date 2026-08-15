package com.attachakki.exception.entityNotFound;

public class OrderItemNotFoundException extends EntityNotFoundException{
    public OrderItemNotFoundException(Long orderItemId) {
        super("OrderItem not found with id "+orderItemId, null);
    }
}
