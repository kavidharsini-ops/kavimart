package com.kavimart.util;

import com.kavimart.exception.ValidationException;

import java.util.Locale;
import java.util.regex.Pattern;

public final class ValidationUtil {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private ValidationUtil() {
    }

    public static String requireText(String value, String fieldName, int maxLength) throws ValidationException {
        if (value == null || value.isBlank()) {
            throw new ValidationException(fieldName + " is required.");
        }
        String normalized = value.trim();
        if (normalized.length() > maxLength) {
            throw new ValidationException(fieldName + " must be at most " + maxLength + " characters.");
        }
        return normalized;
    }

    public static String requireEmail(String value) throws ValidationException {
        String email = requireText(value, "Email", 255).toLowerCase(Locale.ROOT);
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("Enter a valid email address.");
        }
        return email;
    }

    private static final Pattern PASSWORD_HAS_LETTER = Pattern.compile("[A-Za-z]");
    private static final Pattern PASSWORD_HAS_DIGIT = Pattern.compile("[0-9]");

    public static void requirePassword(String password) throws ValidationException {
        if (password == null || password.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters.");
        }
        if (password.length() > 72) {
            throw new ValidationException("Password must be at most 72 characters.");
        }
        if (!PASSWORD_HAS_LETTER.matcher(password).find() || !PASSWORD_HAS_DIGIT.matcher(password).find()) {
            throw new ValidationException("Password must contain at least one letter and one digit.");
        }
    }
}