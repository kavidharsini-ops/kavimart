package com.kavimart.exception;

public class DatabaseException extends AppException {
    public DatabaseException(String message, Throwable cause) {
        super("DATABASE_ERROR", message, 500, cause);
    }
}