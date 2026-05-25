package com.casino.trades.service;

import com.casino.cards.repository.UserCardRepository;
import com.casino.economy.api.EconomyPort;
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
import com.casino.users.dto.response.PublicUserResponse;
import com.casino.users.repository.UserRepository;
import com.casino.users.service.UserService;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.PageRequest;
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
    private final UserService userService;
    private final UserCardRepository userCardRepository;
    private final EconomyPort economyPort;
    private final UserRepository userRepository;

    @Transactional
    public TradeResponse create(long initiatorUserId, CreateTradeRequest req) {
        long partnerUserId = userService.requireByUsername(req.partnerUsername()).getId();
        if (initiatorUserId == partnerUserId) {
            throw new TradeException("Cannot trade with yourself");
        }
        Trade t = tradeRepository.save(new Trade(initiatorUserId, partnerUserId, TradeStatus.DRAFT));
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
        long itemOwner =
                req.fromUserId() != null
                        ? req.fromUserId()
                        : authUserId;
        if (itemOwner != trade.getInitiatorUserId() && itemOwner != trade.getPartnerUserId()) {
            throw new TradeException("Invalid item owner");
        }
        boolean hasCoins = req.coinsAmount() != null && req.coinsAmount() > 0;
        boolean hasCard = req.cardDefinitionId() != null;
        if (hasCoins == hasCard) {
            throw new TradeException("Specify either card or coins");
        }
        if (hasCoins) {
            if (itemOwner != authUserId) {
                throw new TradeException("Only the coin owner can offer coins");
            }
            if (economyPort.getBalance(authUserId) < req.coinsAmount()) {
                throw new TradeException("Insufficient coins");
            }
            tradeItemRepository.save(new TradeItem(tradeId, authUserId, req.coinsAmount()));
        } else {
            int qty = req.quantity() != null ? req.quantity() : 1;
            validateCardOwnership(itemOwner, req.cardDefinitionId(), qty);
            tradeItemRepository.save(new TradeItem(tradeId, itemOwner, req.cardDefinitionId(), qty));
        }
        return tradeMapper.toResponse(trade);
    }

    private void validateCardOwnership(long userId, long cardId, int qty) {
        var row =
                userCardRepository
                        .findByUserIdAndCardDefinitionId(userId, cardId)
                        .orElseThrow(() -> new TradeException("Card not in inventory"));
        if (row.isLocked()) {
            throw new TradeException("Card is locked");
        }
        if (row.getQuantity() < qty) {
            throw new TradeException("Not enough cards");
        }
    }

    @Transactional
    public TradeResponse removeItem(long authUserId, long tradeId, long itemId) {
        Trade trade = tradeRepository.lockById(tradeId).orElseThrow(() -> new TradeException("Not found"));
        if (trade.getStatus() != TradeStatus.DRAFT) {
            throw new TradeException("Trade is not editable");
        }
        if (authUserId != trade.getInitiatorUserId() && authUserId != trade.getPartnerUserId()) {
            throw new TradeException("Forbidden");
        }
        TradeItem item =
                tradeItemRepository
                        .findById(itemId)
                        .filter(i -> i.getTradeId().equals(tradeId))
                        .orElseThrow(() -> new TradeException("Item not found"));
        tradeItemRepository.delete(item);
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
        if (tradeItemRepository.findByTradeId(tradeId).isEmpty()) {
            throw new TradeException("Trade has no items");
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

    @Transactional(readOnly = true)
    public List<PublicUserResponse> participantSuggestions(long userId, int limit) {
        Set<Long> ordered = new LinkedHashSet<>();
        tradeRepository
                .findByInitiatorUserIdOrPartnerUserIdOrderByIdDesc(userId, userId)
                .forEach(
                        t -> {
                            long other =
                                    t.getInitiatorUserId().equals(userId)
                                            ? t.getPartnerUserId()
                                            : t.getInitiatorUserId();
                            ordered.add(other);
                        });
        userRepository
                .findByIdNotAndUsernameIsNotNullOrderByUsernameAsc(userId, PageRequest.of(0, limit))
                .forEach(u -> ordered.add(u.getId()));

        List<PublicUserResponse> result = new ArrayList<>();
        for (Long id : ordered) {
            if (result.size() >= limit) {
                break;
            }
            userRepository
                    .findById(id)
                    .ifPresent(
                            u ->
                                    result.add(
                                            new PublicUserResponse(u.getId(), u.getUsername())));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public TradeResponse get(long userId, long tradeId) {
        Trade trade = tradeRepository.findById(tradeId).orElseThrow(() -> new TradeException("Not found"));
        if (userId != trade.getInitiatorUserId() && userId != trade.getPartnerUserId()) {
            throw new TradeException("Forbidden");
        }
        return tradeMapper.toResponse(trade);
    }
}
