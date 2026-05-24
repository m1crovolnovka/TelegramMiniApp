package com.casino.users.util;

public final class UsernameNormalizer {

    private UsernameNormalizer() {}

    public static String normalize(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username required");
        }
        return username.trim().toLowerCase().replace("@", "");
    }
}
