package com.casino.trades.mapper;

import com.casino.cards.repository.CardDefinitionRepository;
import com.casino.trades.dto.response.TradeItemResponse;
import com.casino.trades.dto.response.TradeResponse;
import com.casino.trades.entity.Trade;
import com.casino.trades.entity.TradeItem;
import com.casino.trades.repository.TradeItemRepository;
import com.casino.users.entity.User;
import com.casino.users.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TradeMapper {

    private final TradeItemRepository tradeItemRepository;
    private final UserRepository userRepository;
    private final CardDefinitionRepository cardDefinitionRepository;

    public TradeResponse toResponse(Trade t) {
        User initiator = userRepository.findById(t.getInitiatorUserId()).orElse(null);
        User partner = userRepository.findById(t.getPartnerUserId()).orElse(null);
        List<TradeItemResponse> items =
                tradeItemRepository.findByTradeId(t.getId()).stream().map(this::toItem).toList();
        return new TradeResponse(
                t.getId(),
                t.getInitiatorUserId(),
                usernameOf(initiator),
                t.getPartnerUserId(),
                usernameOf(partner),
                t.getStatus(),
                items);
    }

    private TradeItemResponse toItem(TradeItem it) {
        String fromUsername =
                userRepository
                        .findById(it.getFromUserId())
                        .map(User::getUsername)
                        .orElse(String.valueOf(it.getFromUserId()));
        String cardTitle = null;
        if (it.getCardDefinitionId() != null) {
            cardTitle =
                    cardDefinitionRepository
                            .findById(it.getCardDefinitionId())
                            .map(c -> c.getTitle())
                            .orElse(null);
        }
        return new TradeItemResponse(
                it.getId(),
                it.getFromUserId(),
                fromUsername,
                it.getCardDefinitionId(),
                cardTitle,
                it.getQuantity(),
                it.getCoinsAmount());
    }

    private static String usernameOf(User user) {
        return user != null && user.getUsername() != null ? user.getUsername() : "?";
    }
}
