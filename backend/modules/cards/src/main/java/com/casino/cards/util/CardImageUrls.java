package com.casino.cards.util;

public final class CardImageUrls {

    private CardImageUrls() {}

    public static String resolve(String imageStorageKey) {
        if (imageStorageKey == null || imageStorageKey.isBlank()) {
            return null;
        }
        String key = imageStorageKey.trim();
        if (key.startsWith("http://") || key.startsWith("https://")) {
            return key;
        }
        return null;
    }
}
