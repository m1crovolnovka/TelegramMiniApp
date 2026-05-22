package com.casino.trades.service;

import com.casino.cards.api.InventoryPort;
import com.casino.economy.api.EconomyPort;
import com.casino.economy.entity.TransactionType;
import com.casino.trades.entity.Trade;
import com.casino.trades.entity.TradeItem;
import com.casino.trades.entity.TradeStatus;
import com.casino.trades.exception.TradeException;
import com.casino.trades.repository.TradeItemRepository;
import com.casino.trades.repository.TradeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TradeExecutionService {

    private final TradeRepository tradeRepository;
    private final TradeItemRepository tradeItemRepository;
    private final InventoryPort inventoryPort;
    private final EconomyPort economyPort;

    @Transactional
    public void executeSwap(long tradeId) {
        Trade trade = tradeRepository.lockById(tradeId).orElseThrow(() -> new TradeException("Trade not found"));
        if (trade.getStatus() != TradeStatus.PENDING) {
            throw new TradeException("Trade is not pending");
        }
        List<TradeItem> items = tradeItemRepository.findByTradeId(tradeId);
        String transferBase = "trade:" + tradeId;
        for (TradeItem it : items) {
            long toUserId =
                    it.getFromUserId().equals(trade.getInitiatorUserId())
                            ? trade.getPartnerUserId()
                            : trade.getInitiatorUserId();
            if (it.isCoins()) {
                economyPort.transfer(
                        it.getFromUserId(),
                        toUserId,
                        it.getCoinsAmount(),
                        transferBase + ":coins:" + it.getId(),
                        "transfer:" + it.getFromUserId() + ":" + toUserId);
            } else {
                inventoryPort.removeCard(it.getFromUserId(), it.getCardDefinitionId(), it.getQuantity());
                inventoryPort.addCard(toUserId, it.getCardDefinitionId(), it.getQuantity());
            }
        }
        trade.setStatus(TradeStatus.COMPLETED);
    }
}
