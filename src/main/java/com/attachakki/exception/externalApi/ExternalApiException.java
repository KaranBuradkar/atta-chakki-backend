package com.attachakki.exception.externalApi;

import com.attachakki.exception.AppException;

public class ExternalApiException extends AppException {

    public ExternalApiException(String message, Object data) {
        super(message, data);
    }

}
