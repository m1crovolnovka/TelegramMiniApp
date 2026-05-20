package com.casino.trades.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateTradeRequest(@NotNull Long partnerUserId) {}
