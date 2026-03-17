package com.atachakki.controller;

import com.atachakki.dto.ErrorResponse;
import com.atachakki.exception.AppException;
import com.atachakki.exception.businessLogic.BusinessLogicException;
import com.atachakki.exception.entityNotFound.EntityNotFoundException;
import com.atachakki.exception.validation.InvalidJwtTokenException;
import com.atachakki.exception.validation.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Centralized exception handler for application-level errors.
 */
@ControllerAdvice
public class GobleExceptionHandlerController {

    private static final Logger log = LoggerFactory.getLogger(GobleExceptionHandlerController.class);
    private final HttpServletRequest request;

    public GobleExceptionHandlerController(HttpServletRequest request) {
        this.request = request;
    }

    private <T> ResponseEntity<ErrorResponse<T>> errorResponse(HttpStatus status, String message, T data) {
        return ResponseEntity
                .status(status)
                .body(new ErrorResponse<>(message, data, request.getRequestURI()));
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse<Object>> handleAppException(AppException e) {
        log.error("Application exception", e);
        return errorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), e.getData());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse<Object>> handleValidationException(ValidationException e) {
        log.error("Validation failed", e);
        return errorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), e.getData());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse<Map<String, String>>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        log.error("Validation errors: {}", errors);
        return errorResponse(HttpStatus.BAD_REQUEST, "Validation failed", errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse<Map<String, String>>> handleConstraintViolation(ConstraintViolationException e) {
        Map<String, String> errors = new HashMap<>();
        e.getConstraintViolations().forEach(v ->
                errors.put(v.getPropertyPath().toString(), v.getMessage())
        );
        log.error("Constraint violation: {}", errors);
        return errorResponse(HttpStatus.BAD_REQUEST, "Invalid input", errors);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse<Void>> handleMissingParam(MissingServletRequestParameterException e) {
        log.error("Missing parameter: {}", e.getParameterName());
        return errorResponse(HttpStatus.BAD_REQUEST, "Missing parameter: " + e.getParameterName(), null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse<Void>> handleUnreadableMessage(HttpMessageNotReadableException e) {
        log.error("Malformed JSON", e);
        return errorResponse(HttpStatus.BAD_REQUEST, "Malformed JSON. Check request body.", null);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse<Void>> handleBadCredentials(BadCredentialsException e) {
        log.warn("Invalid credentials", e);
        return errorResponse(HttpStatus.UNAUTHORIZED, "Invalid username or password", null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse<Void>> handleAccessDenied(AccessDeniedException e) {
        log.warn("Access denied", e);
        return errorResponse(HttpStatus.FORBIDDEN, "You do not have permission", null);
    }

    @ExceptionHandler(InvalidJwtTokenException.class)
    public ResponseEntity<ErrorResponse<String>> handleInvalidJwtToken(InvalidJwtTokenException e) {
        log.warn("Invalid JWT token", e);
        return errorResponse(HttpStatus.UNAUTHORIZED, "Invalid or expired JWT token", e.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse<Void>> handleNoResourceFound(NoResourceFoundException e) {
        log.warn("Resource not found: {}", request.getRequestURI(), e);
        return errorResponse(HttpStatus.BAD_REQUEST, "No such resource", null);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse<Object>> handleEntityNotFound(EntityNotFoundException e) {
        log.warn("Entity not found: {}", e.getMessage());
        return errorResponse(HttpStatus.NOT_FOUND, e.getMessage(), e.getData());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse<String>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.warn("Method argument type mismatch", e);
        String details = String.format(
                "Check input for parameter '%s'. Provided '%s', expected '%s'",
                e.getPropertyName(), e.getValue(), e.getRequiredType()
        );
        return errorResponse(HttpStatus.BAD_REQUEST, "Method argument type mismatch", details);
    }

    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ErrorResponse<Object>> handleBusinessLogicException(BusinessLogicException e) {
        log.warn("Business logic exception: {}", e.getMessage(), e);
        return errorResponse(HttpStatus.NOT_FOUND, e.getMessage(), e.getData());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.error("Data integrity violation", e);
        return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Database constraint violation", null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse<String>> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.warn("Request method not supported", e);
        String details = String.format(
                "Method '%s' not supported for endpoint '%s'. Supported: %s",
                e.getMethod(), request.getRequestURL(), e.getSupportedHttpMethods()
        );
        return errorResponse(HttpStatus.BAD_REQUEST, "Request method not supported", details);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse<String>> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException e) {
        String supported = e.getSupportedMediaTypes().toString();
        return errorResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "Media type not supported",
                "Supported: " + supported);
    }

    @ExceptionHandler(ServletException.class)
    public ResponseEntity<ErrorResponse<String>> handleServletException(ServletException e) {
        log.error("unexpected servlet exception happened", e);
        return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Servlet exception", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse<Void>> handleGenericException(Exception e) {
        log.error("Unexpected error", e);
        return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred", null);
    }
}