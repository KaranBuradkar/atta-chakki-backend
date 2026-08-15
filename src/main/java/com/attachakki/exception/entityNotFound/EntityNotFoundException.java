package com.attachakki.exception.entityNotFound;

import com.attachakki.exception.AppException;

public class EntityNotFoundException extends AppException {
    public EntityNotFoundException(String message, Object data) {
        super(message, data);
    }
}
