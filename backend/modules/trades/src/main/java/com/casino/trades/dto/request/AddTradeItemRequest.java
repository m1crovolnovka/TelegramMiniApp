package com.casino.trades.dto.request;

import jakarta.validation.constraints.Min;

public record AddTradeItemRequest(
        Long fromUserId,
        Long cardDefinitionId,
        @Min(1) Integer quantity,
        @Min(1) Long coinsAmount) {}
