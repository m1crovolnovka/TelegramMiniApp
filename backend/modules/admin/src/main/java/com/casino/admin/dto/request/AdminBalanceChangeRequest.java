package com.casino.admin.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminBalanceChangeRequest(
        @NotNull Long userId, @NotNull @Min(1) Long amount, @NotBlank String reason) {}
