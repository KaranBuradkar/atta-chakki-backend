package com.attachakki.dto;

public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;
    private final Long timestamp;
    private final String path;

    public ApiResponse(boolean success, String message, T data, String path) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.path = path;
        this.timestamp = System.currentTimeMillis();
    }

    public ApiResponse(boolean success, String message, String path) {
        this(success, message, null, path);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getPath() {
        return path;
    }
}
