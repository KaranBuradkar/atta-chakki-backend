package com.attachakki.exception.validation;

public class PaymentValidationFailed extends ValidationException {
    public PaymentValidationFailed(String message) {
        this(message, null);
    }
    public PaymentValidationFailed(String message, String details) {
        super(message, details);
    }
}
