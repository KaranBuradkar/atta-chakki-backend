package com.attachakki.controller;

import com.attachakki.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class BaseController {

    protected final HttpServletRequest request;

    protected BaseController(HttpServletRequest request) {
        this.request = request;
    }

    private <T>ApiResponse<T> apiResponse(String message, T data) {
        return new ApiResponse<>(true, message, data, request.getRequestURI());
    }

    protected <T>ResponseEntity<ApiResponse<T>> apiResponse(HttpStatus status, String message, T data) {
        return ResponseEntity.status(status)
                .body(apiResponse(message, data));
    }
}
