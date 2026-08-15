package com.attachakki.exception.entityNotFound;

public class UserDetailsNotFoundException extends EntityNotFoundException {
    public UserDetailsNotFoundException(String message) {
        super(message, null);
    }
}
