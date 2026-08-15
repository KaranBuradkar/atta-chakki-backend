package com.attachakki.exception.entityNotFound;

public class PaymentNotFoundException extends EntityNotFoundException{
    public PaymentNotFoundException(Long paymentId) {
        super("Payment not found with payment id "+paymentId, null);
    }
}
