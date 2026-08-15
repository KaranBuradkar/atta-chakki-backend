package com.attachakki.exception.businessLogic;

import com.attachakki.exception.AppException;

public class BusinessLogicException extends AppException {

    public BusinessLogicException(String message, Object data) {
        super(message, data);
    }

}
