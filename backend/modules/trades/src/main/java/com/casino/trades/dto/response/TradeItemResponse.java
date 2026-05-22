package com.casino.trades.dto.response;

public record TradeItemResponse(
        long fromUserId, Long cardDefinitionId, int quantity, Long coinsAmount) {}
