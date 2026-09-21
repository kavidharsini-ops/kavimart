package com.kavimart.exception;

public class AppException extends Exception {
    private final String code;
    private final int status;

    public AppException(String code, String message, int status) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public AppException(String code, String message, int status, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public int getStatus() {
        return status;
    }
}