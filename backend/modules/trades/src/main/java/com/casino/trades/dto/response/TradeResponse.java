package com.casino.trades.dto.response;

import com.casino.trades.entity.TradeStatus;
import java.util.List;

public record TradeResponse(
        long id,
        long initiatorUserId,
        String initiatorUsername,
        long partnerUserId,
        String partnerUsername,
        TradeStatus status,
        List<TradeItemResponse> items) {}
