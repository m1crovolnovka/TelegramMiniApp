package com.casino.trades.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateTradeRequest(@NotBlank String partnerUsername) {}
