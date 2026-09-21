package com.kavimart.exception;

public class ValidationException extends AppException {
    public ValidationException(String message) {
        super("VALIDATION_ERROR", message, 400);
    }
}