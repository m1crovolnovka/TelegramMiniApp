package com.casino.packs.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OpenPackRequest(
        @NotNull @Positive Long packId,
        String idempotencyKey) {}
