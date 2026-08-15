package com.attachakki.exception.businessLogic;

public class CustomerIsBlockedException extends BusinessLogicException {
    public CustomerIsBlockedException(Long customerId) {
        super(String.format("Customer id %s is blocked, Unblock first to place order", customerId),
                "If customer is block in that case can not place any order");
    }
}
