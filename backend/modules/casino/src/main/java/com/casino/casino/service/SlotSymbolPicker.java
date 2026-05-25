package com.casino.casino.service;

import java.util.List;

final class SlotSymbolPicker {

    private SlotSymbolPicker() {}

    static List<String> pickSymbols(String variant, double multiplier) {
        return switch (normalize(variant)) {
            case "dog-house" -> symbolsForMultiplier(multiplier, List.of("🐕", "🦴", "🏠", "🐾", "💎"), "🐕");
            case "gates-olympus" ->
                    symbolsForMultiplier(multiplier, List.of("⚡", "👑", "🏛️", "💎", "🔱"), "⚡");
            default -> symbolsForMultiplier(multiplier, List.of("🍇", "🍬", "💣", "🍭", "💎"), "🍬");
        };
    }

    private static String normalize(String variant) {
        return variant == null ? "sweet-bonanza" : variant.trim().toLowerCase();
    }

    private static List<String> symbolsForMultiplier(
            double multiplier, List<String> pool, String defaultSymbol) {
        if (multiplier <= 0) {
            return List.of(pick(pool, 0), pick(pool, 1), pick(pool, 2));
        }
        if (multiplier >= 10) {
            String win = pool.get(pool.size() - 1);
            return List.of(win, win, win);
        }
        if (multiplier >= 3) {
            String win = pool.get(Math.min(3, pool.size() - 1));
            return List.of(win, win, win);
        }
        String win = pool.get(Math.min(1, pool.size() - 1));
        return List.of(win, win, win);
    }

    private static String pick(List<String> pool, int index) {
        return pool.get(index % pool.size());
    }
}
