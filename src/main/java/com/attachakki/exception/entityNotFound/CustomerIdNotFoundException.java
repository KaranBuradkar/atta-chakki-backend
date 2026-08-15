package com.attachakki.exception.entityNotFound;

public class CustomerIdNotFoundException extends EntityNotFoundException {
    public CustomerIdNotFoundException(Long customerId) {
        super("customer id "+customerId+" not found", null);
    }
}
