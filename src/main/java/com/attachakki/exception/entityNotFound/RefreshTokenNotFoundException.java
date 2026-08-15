package com.attachakki.exception.entityNotFound;

public class RefreshTokenNotFoundException extends EntityNotFoundException{
    public RefreshTokenNotFoundException(String message, Object data) {
        super(message, data);
    }
}
