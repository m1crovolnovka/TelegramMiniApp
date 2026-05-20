package com.casino.common.rng;

import java.security.SecureRandom;
import java.util.List;

/**
 * Stateless weighted selection (pack drops, slot outcomes, etc.).
 */
public final class WeightedRandomSelector {

    private static final SecureRandom RND = new SecureRandom();

    private WeightedRandomSelector() {}

    public static <T> T pick(List<T> items, List<Integer> weights) {
        if (items.isEmpty() || items.size() != weights.size()) {
            throw new IllegalArgumentException("items and weights must be same non-empty size");
        }
        long total = 0;
        for (int w : weights) {
            if (w < 0) {
                throw new IllegalArgumentException("negative weight");
            }
            total += w;
        }
        if (total <= 0) {
            throw new IllegalArgumentException("total weight must be positive");
        }
        long r = nextLongExclusive(total);
        long acc = 0;
        for (int i = 0; i < items.size(); i++) {
            acc += weights.get(i);
            if (r < acc) {
                return items.get(i);
            }
        }
        return items.getLast();
    }

    private static long nextLongExclusive(long bound) {
        long r = RND.nextLong();
        long m = bound - 1;
        if ((bound & m) == 0L) {
            return r & m;
        }
        long u = r >>> 1;
        while (u + (m - u % m) < 0L) {
            u = RND.nextLong() >>> 1;
        }
        return u % bound;
    }
}
