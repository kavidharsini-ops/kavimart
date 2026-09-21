package com.kavimart.exception;

public class DuplicateEmailException extends AppException {
    public DuplicateEmailException() {
        super("EMAIL_ALREADY_REGISTERED", "An account with that email already exists.", 409);
    }
}