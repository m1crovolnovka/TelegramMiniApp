package com.casino.cards.service;

import com.casino.cards.dto.response.CardDefinitionResponse;
import com.casino.cards.dto.response.InventoryItemResponse;
import com.casino.cards.dto.response.InventoryResponse;
import com.casino.cards.entity.CardDefinition;
import com.casino.cards.entity.UserCard;
import com.casino.cards.mapper.CardMapper;
import com.casino.cards.repository.CardDefinitionRepository;
import com.casino.cards.repository.UserCardRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CardCatalogService {

    private final CardDefinitionRepository cardDefinitionRepository;
    private final UserCardRepository userCardRepository;
    private final CardMapper cardMapper;

    @Transactional(readOnly = true)
    public List<CardDefinitionResponse> listDefinitions() {
        return cardDefinitionRepository.findAll().stream().map(cardMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public InventoryResponse inventory(long userId) {
        List<UserCard> owned = userCardRepository.findByUserId(userId);
        if (owned.isEmpty()) {
            return new InventoryResponse(List.of(), 0);
        }
        Map<Long, CardDefinition> defs =
                cardDefinitionRepository.findAllById(
                                owned.stream().map(UserCard::getCardDefinitionId).distinct().toList())
                        .stream()
                        .collect(Collectors.toMap(CardDefinition::getId, d -> d));
        List<InventoryItemResponse> items =
                owned.stream()
                        .map(
                                uc -> {
                                    CardDefinition def = defs.get(uc.getCardDefinitionId());
                                    String title = def != null ? def.getTitle() : "Unknown";
                                    var rarity = def != null ? def.getRarity() : null;
                                    return new InventoryItemResponse(
                                            uc.getCardDefinitionId(),
                                            title,
                                            rarity,
                                            uc.getQuantity(),
                                            uc.isLocked(),
                                            uc.getLockedTradeId());
                                })
                        .toList();
        int total = items.stream().mapToInt(InventoryItemResponse::quantity).sum();
        return new InventoryResponse(items, total);
    }
}
