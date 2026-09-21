package com.kavimart.exception;

public class AuthenticationException extends AppException {
    public AuthenticationException() {
        super("INVALID_CREDENTIALS", "Email or password is incorrect.", 401);
    }
}