package com.casino.trades.service;

import com.casino.trades.dto.request.AddTradeItemRequest;
import com.casino.trades.dto.request.CreateTradeRequest;
import com.casino.trades.dto.response.TradeResponse;
import com.casino.trades.entity.Trade;
import com.casino.trades.entity.TradeItem;
import com.casino.trades.entity.TradeStatus;
import com.casino.trades.exception.TradeException;
import com.casino.trades.mapper.TradeMapper;
import com.casino.trades.repository.TradeItemRepository;
import com.casino.trades.repository.TradeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository tradeRepository;
    private final TradeItemRepository tradeItemRepository;
    private final TradeMapper tradeMapper;
    private final TradeExecutionService tradeExecutionService;

    @Transactional
    public TradeResponse create(long initiatorUserId, CreateTradeRequest req) {
        Trade t =
                tradeRepository.save(
                        new Trade(initiatorUserId, req.partnerUserId(), TradeStatus.DRAFT));
        return tradeMapper.toResponse(t);
    }

    @Transactional
    public TradeResponse addItem(long authUserId, long tradeId, AddTradeItemRequest req) {
        Trade trade = tradeRepository.lockById(tradeId).orElseThrow(() -> new TradeException("Not found"));
        if (trade.getStatus() != TradeStatus.DRAFT) {
            throw new TradeException("Trade is not editable");
        }
        if (authUserId != trade.getInitiatorUserId() && authUserId != trade.getPartnerUserId()) {
            throw new TradeException("Forbidden");
        }
        tradeItemRepository.save(
                new TradeItem(tradeId, authUserId, req.cardDefinitionId(), req.quantity()));
        return tradeMapper.toResponse(trade);
    }

    @Transactional
    public TradeResponse send(long initiatorUserId, long tradeId) {
        Trade trade = tradeRepository.lockById(tradeId).orElseThrow(() -> new TradeException("Not found"));
        if (!trade.getInitiatorUserId().equals(initiatorUserId)) {
            throw new TradeException("Only initiator can send");
        }
        if (trade.getStatus() != TradeStatus.DRAFT) {
            throw new TradeException("Invalid status");
        }
        trade.setStatus(TradeStatus.PENDING);
        return tradeMapper.toResponse(trade);
    }

    @Transactional
    public TradeResponse accept(long partnerUserId, long tradeId) {
        Trade trade = tradeRepository.lockById(tradeId).orElseThrow(() -> new TradeException("Not found"));
        if (!trade.getPartnerUserId().equals(partnerUserId)) {
            throw new TradeException("Only partner can accept");
        }
        if (trade.getStatus() != TradeStatus.PENDING) {
            throw new TradeException("Invalid status");
        }
        tradeExecutionService.executeSwap(tradeId);
        return tradeMapper.toResponse(tradeRepository.findById(tradeId).orElseThrow());
    }

    @Transactional
    public void reject(long partnerUserId, long tradeId) {
        Trade trade = tradeRepository.lockById(tradeId).orElseThrow(() -> new TradeException("Not found"));
        if (!trade.getPartnerUserId().equals(partnerUserId)) {
            throw new TradeException("Only partner can reject");
        }
        trade.setStatus(TradeStatus.REJECTED);
    }

    @Transactional(readOnly = true)
    public List<TradeResponse> history(long userId) {
        return tradeRepository.findByInitiatorUserIdOrPartnerUserIdOrderByIdDesc(userId, userId).stream()
                .map(tradeMapper::toResponse)
                .toList();
    }
}
