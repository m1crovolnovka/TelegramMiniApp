package com.casino.packs.mapper;

import com.casino.cards.entity.CardDefinition;
import com.casino.cards.entity.CardRarity;
import com.casino.cards.repository.CardDefinitionRepository;
import com.casino.cards.util.CardImageUrls;
import com.casino.packs.dto.response.DroppedCardResponse;
import com.casino.packs.dto.response.PackOpenHistoryItemResponse;
import com.casino.packs.dto.response.PackResponse;
import com.casino.packs.entity.Pack;
import com.casino.packs.entity.PackBundleSlot;
import com.casino.packs.entity.PackKind;
import com.casino.packs.entity.PackOpenHistory;
import com.casino.packs.repository.PackBundleSlotRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PackMapper {

    private final CardDefinitionRepository cardDefinitionRepository;
    private final PackBundleSlotRepository packBundleSlotRepository;

    public PackResponse toResponse(Pack p) {
        String summary = null;
        if (p.getPackKind() == PackKind.BUNDLE) {
            List<PackBundleSlot> slots = packBundleSlotRepository.findByPackIdOrderByIdAsc(p.getId());
            summary =
                    slots.stream()
                            .map(s -> s.getQuantity() + " " + rarityLabel(s.getRarity()))
                            .collect(Collectors.joining(" + "));
        }
        return new PackResponse(
                p.getId(),
                p.getName(),
                p.getPriceCoins(),
                p.getPackKind().name(),
                summary);
    }

    private static String rarityLabel(CardRarity rarity) {
        return switch (rarity) {
            case COMMON -> "обыч.";
            case RARE -> "редк.";
            case LEGENDARY -> "легенд.";
        };
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
