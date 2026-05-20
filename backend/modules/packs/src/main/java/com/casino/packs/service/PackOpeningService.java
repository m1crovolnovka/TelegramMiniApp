package com.casino.packs.service;

import com.casino.cards.api.InventoryPort;
import com.casino.cards.entity.CardDefinition;
import com.casino.cards.repository.CardDefinitionRepository;
import com.casino.economy.api.EconomyPort;
import com.casino.economy.entity.TransactionType;
import com.casino.packs.dto.response.DroppedCardResponse;
import com.casino.packs.dto.response.OpenPackResponse;
import com.casino.packs.entity.Pack;
import com.casino.packs.entity.PackOpenHistory;
import com.casino.packs.exception.PackNotFoundException;
import com.casino.packs.repository.PackOpenHistoryRepository;
import com.casino.packs.repository.PackRepository;
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
        String idem = clientIdempotencyKey != null && !clientIdempotencyKey.isBlank()
                ? clientIdempotencyKey
                : "auto-" + UUID.randomUUID();

        if (packOpenHistoryRepository.existsByUserIdAndIdempotencyKey(userId, idem)) {
            PackOpenHistory h =
                    packOpenHistoryRepository.findByUserIdAndIdempotencyKey(userId, idem).orElseThrow();
            CardDefinition c =
                    cardDefinitionRepository
                            .findById(h.getDroppedCardDefinitionId())
                            .orElseThrow(() -> new IllegalStateException("Dropped card missing"));
            return toResponse(c);
        }

        Pack pack = packRepository.findById(packId).orElseThrow(PackNotFoundException::new);
        String debitOp = "pack-open-debit:" + idem;
        economyPort.debit(userId, pack.getPriceCoins(), debitOp, TransactionType.PACK_PURCHASE, "pack_open");

        CardDefinition rolled = dropCalculationService.rollCard(packId);
        inventoryPort.addCard(userId, rolled.getId(), 1);
        packOpenHistoryRepository.save(new PackOpenHistory(userId, packId, rolled.getId(), idem));
        return toResponse(rolled);
    }

    private OpenPackResponse toResponse(CardDefinition c) {
        return new OpenPackResponse(new DroppedCardResponse(c.getId(), c.getTitle(), c.getRarity()));
    }
}
