package com.kavimart.util;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordUtil {
    private PasswordUtil() {
    }

    public static String hash(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(12));
    }

    public static boolean matches(String rawPassword, String passwordHash) {
        try {
            return BCrypt.checkpw(rawPassword, passwordHash);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }
}