package com.casino.trades.mapper;

import com.casino.trades.dto.response.TradeItemResponse;
import com.casino.trades.dto.response.TradeResponse;
import com.casino.trades.entity.Trade;
import com.casino.trades.entity.TradeItem;
import com.casino.trades.repository.TradeItemRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TradeMapper {

    private final TradeItemRepository tradeItemRepository;

    public TradeResponse toResponse(Trade t) {
        List<TradeItemResponse> items =
                tradeItemRepository.findByTradeId(t.getId()).stream().map(this::toItem).toList();
        return new TradeResponse(
                t.getId(), t.getInitiatorUserId(), t.getPartnerUserId(), t.getStatus(), items);
    }

    private TradeItemResponse toItem(TradeItem it) {
        return new TradeItemResponse(
                it.getFromUserId(), it.getCardDefinitionId(), it.getQuantity(), it.getCoinsAmount());
    }
}
