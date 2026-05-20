package com.casino.trades.dto.response;

import com.casino.trades.entity.TradeStatus;

public record TradeResponse(long id, long initiatorUserId, long partnerUserId, TradeStatus status) {}
