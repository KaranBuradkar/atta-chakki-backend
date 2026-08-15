package com.attachakki.exception.businessLogic;

public class OrderItemNameAlreadyExistException extends BusinessLogicException {
    public OrderItemNameAlreadyExistException(String name) {
        super("OrderItem name - "+name+" already exist", null);
    }
}
