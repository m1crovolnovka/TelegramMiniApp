package com.casino.trades.mapper;

import com.casino.trades.dto.response.TradeResponse;
import com.casino.trades.entity.Trade;
import org.springframework.stereotype.Component;

@Component
public class TradeMapper {

    public TradeResponse toResponse(Trade t) {
        return new TradeResponse(t.getId(), t.getInitiatorUserId(), t.getPartnerUserId(), t.getStatus());
    }
}
