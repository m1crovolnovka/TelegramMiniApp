package com.casino.casino.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SpinRequest(@NotNull @Min(1) Long bet, String variant) {

    public String resolvedVariant() {
        if (variant == null || variant.isBlank()) {
            return "sweet-bonanza";
        }
        return switch (variant.trim().toLowerCase()) {
            case "dog-house", "gates-olympus", "sweet-bonanza" -> variant.trim().toLowerCase();
            default -> "sweet-bonanza";
        };
    }
}
