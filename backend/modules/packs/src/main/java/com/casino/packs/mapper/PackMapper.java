package com.casino.packs.mapper;

import com.casino.cards.entity.CardDefinition;
import com.casino.cards.repository.CardDefinitionRepository;
import com.casino.cards.util.CardImageUrls;
import com.casino.packs.dto.response.DroppedCardResponse;
import com.casino.packs.dto.response.PackOpenHistoryItemResponse;
import com.casino.packs.dto.response.PackResponse;
import com.casino.packs.entity.Pack;
import com.casino.packs.entity.PackOpenHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PackMapper {

    private final CardDefinitionRepository cardDefinitionRepository;

    public PackResponse toResponse(Pack p) {
        return new PackResponse(p.getId(), p.getName(), p.getPriceCoins());
    }

    public PackOpenHistoryItemResponse toHistoryItem(PackOpenHistory h) {
        CardDefinition c =
                cardDefinitionRepository
                        .findById(h.getDroppedCardDefinitionId())
                        .orElse(null);
        DroppedCardResponse dropped =
                c == null
                        ? new DroppedCardResponse(h.getDroppedCardDefinitionId(), "?", null, null)
                        : new DroppedCardResponse(
                                c.getId(), c.getTitle(), c.getRarity(), CardImageUrls.resolve(c.getImageStorageKey()));
        return new PackOpenHistoryItemResponse(
                h.getId(), h.getPackId(), dropped, h.getCreatedAt());
    }
}
