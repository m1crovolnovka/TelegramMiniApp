package com.casino.betting.dto.request;

import jakarta.validation.constraints.NotNull;

public record SettleBettingEventRequest(@NotNull Long winningOptionId) {}
