package com.attachakki.dto;

public class ErrorResponse<T> {

    private final boolean success;
    private final String message;
    private final T details;
    private final Long timestamp;
    private final String path;

    public ErrorResponse(String message, T data, String path) {
        this.success = false;
        this.message = message;
        this.details = data;
        this.timestamp = System.currentTimeMillis();
        this.path = path;
    }

    public ErrorResponse(String message, String path) {
        this(message, null, path);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Object getDetails() {
        return details;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getPath() {
        return path;
    }
}
