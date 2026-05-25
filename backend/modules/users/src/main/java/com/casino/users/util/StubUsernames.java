package com.casino.users.util;

public final class StubUsernames {

    private StubUsernames() {}

    public static boolean isStub(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }
        String n = username.trim().toLowerCase();
        return n.equals("admin") || n.equals("player");
    }
}
