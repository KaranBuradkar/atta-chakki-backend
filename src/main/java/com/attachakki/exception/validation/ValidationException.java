package com.attachakki.exception.validation;

import com.attachakki.exception.AppException;

public class ValidationException extends AppException {

    public ValidationException(String message, Object data) {
        super(message, data);
    }

}
