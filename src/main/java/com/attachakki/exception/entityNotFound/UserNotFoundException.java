package com.attachakki.exception.entityNotFound;

public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException(String message, Object data) {
        super(message, data);
    }
}
