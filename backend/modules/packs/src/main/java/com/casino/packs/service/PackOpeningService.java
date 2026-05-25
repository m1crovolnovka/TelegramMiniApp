package com.casino.packs.service;

import com.casino.cards.api.InventoryPort;
import com.casino.cards.entity.CardDefinition;
import com.casino.cards.repository.CardDefinitionRepository;
import com.casino.cards.util.CardImageUrls;
import com.casino.economy.api.EconomyPort;
import com.casino.economy.entity.TransactionType;
import com.casino.packs.dto.response.DroppedCardResponse;
import com.casino.packs.dto.response.OpenPackResponse;
import com.casino.packs.entity.Pack;
import com.casino.packs.entity.PackKind;
import com.casino.packs.entity.PackOpenHistory;
import com.casino.packs.exception.PackNotFoundException;
import com.casino.packs.repository.PackOpenHistoryRepository;
import com.casino.packs.repository.PackRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PackOpeningService {

    private final PackRepository packRepository;
    private final PackOpenHistoryRepository packOpenHistoryRepository;
    private final DropCalculationService dropCalculationService;
    private final EconomyPort economyPort;
    private final InventoryPort inventoryPort;
    private final CardDefinitionRepository cardDefinitionRepository;

    @Transactional
    public OpenPackResponse openPack(long userId, long packId, String clientIdempotencyKey) {
        String idem =
                clientIdempotencyKey != null && !clientIdempotencyKey.isBlank()
                        ? clientIdempotencyKey
                        : "auto-" + UUID.randomUUID();

        Pack pack = packRepository.findById(packId).orElseThrow(PackNotFoundException::new);
        if (pack.getPackKind() == PackKind.BUNDLE) {
            return openBundle(userId, pack, idem);
        }
        return openSingle(userId, pack, packId, idem);
    }

    private OpenPackResponse openSingle(long userId, Pack pack, long packId, String idem) {
        if (packOpenHistoryRepository.existsByUserIdAndIdempotencyKey(userId, idem)) {
            PackOpenHistory h =
                    packOpenHistoryRepository.findByUserIdAndIdempotencyKey(userId, idem).orElseThrow();
            CardDefinition c =
                    cardDefinitionRepository
                            .findById(h.getDroppedCardDefinitionId())
                            .orElseThrow(() -> new IllegalStateException("Dropped card missing"));
            return toSingleResponse(c);
        }

        String debitOp = "pack-open-debit:" + idem;
        economyPort.debit(userId, pack.getPriceCoins(), debitOp, TransactionType.PACK_PURCHASE, "pack_open");

        CardDefinition rolled = dropCalculationService.rollCard(packId);
        inventoryPort.addCard(userId, rolled.getId(), 1);
        packOpenHistoryRepository.save(new PackOpenHistory(userId, packId, rolled.getId(), idem));
        return toSingleResponse(rolled);
    }

    private OpenPackResponse openBundle(long userId, Pack pack, String idem) {
        String firstKey = idem + ":0";
        if (packOpenHistoryRepository.existsByUserIdAndIdempotencyKey(userId, firstKey)) {
            return rebuildBundleResponse(userId, idem);
        }

        String debitOp = "pack-open-debit:" + idem;
        economyPort.debit(userId, pack.getPriceCoins(), debitOp, TransactionType.PACK_PURCHASE, "pack_open");

        List<CardDefinition> rolled = dropCalculationService.rollBundle(pack.getId());
        int index = 0;
        for (CardDefinition card : rolled) {
            inventoryPort.addCard(userId, card.getId(), 1);
            packOpenHistoryRepository.save(
                    new PackOpenHistory(userId, pack.getId(), card.getId(), idem + ":" + index++));
        }
        return toBundleResponse(rolled);
    }

    private OpenPackResponse rebuildBundleResponse(long userId, String idemPrefix) {
        List<PackOpenHistory> rows =
                packOpenHistoryRepository.findByUserIdAndIdempotencyKeyStartingWith(userId, idemPrefix + ":");
        rows.sort(Comparator.comparing(PackOpenHistory::getIdempotencyKey));
        List<CardDefinition> cards = new ArrayList<>();
        for (PackOpenHistory h : rows) {
            cardDefinitionRepository
                    .findById(h.getDroppedCardDefinitionId())
                    .ifPresent(cards::add);
        }
        return toBundleResponse(cards);
    }

    private OpenPackResponse toSingleResponse(CardDefinition c) {
        DroppedCardResponse card = toDropped(c);
        return new OpenPackResponse(PackKind.SINGLE.name(), card, List.of(card));
    }

    private OpenPackResponse toBundleResponse(List<CardDefinition> cards) {
        List<DroppedCardResponse> dropped = cards.stream().map(this::toDropped).toList();
        DroppedCardResponse first = dropped.isEmpty() ? null : dropped.get(0);
        return new OpenPackResponse(PackKind.BUNDLE.name(), first, dropped);
    }

    private DroppedCardResponse toDropped(CardDefinition c) {
        return new DroppedCardResponse(
                c.getId(), c.getTitle(), c.getRarity(), CardImageUrls.resolve(c.getImageStorageKey()));
    }
}
