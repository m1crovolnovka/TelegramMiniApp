package com.casino.trades.dto.response;

public record TradeItemResponse(
        long fromUserId,
        String fromUsername,
        Long cardDefinitionId,
        String cardTitle,
        int quantity,
        Long coinsAmount) {}
