package com.attachakki.exception;

public class AppException extends RuntimeException{

    private final Object data;

    public AppException(String message, Object data) {
        super(message);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
