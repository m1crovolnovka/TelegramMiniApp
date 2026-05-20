package com.casino.trades.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddTradeItemRequest(@NotNull Long cardDefinitionId, @NotNull @Min(1) Integer quantity) {}
