package com.casino.packs.service;

import com.casino.cards.entity.CardDefinition;
import com.casino.cards.repository.CardDefinitionRepository;
import com.casino.cards.entity.CardRarity;
import com.casino.packs.entity.Pack;
import com.casino.packs.entity.PackDropRow;
import com.casino.packs.repository.PackDropRowRepository;
import com.casino.packs.repository.PackRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DropCalculationService {

    private final PackDropRowRepository packDropRowRepository;
    private final PackRepository packRepository;
    private final CardDefinitionRepository cardDefinitionRepository;
    private final RandomService randomService;

    public CardDefinition rollCard(long packId) {
        Pack pack = packRepository.findById(packId).orElse(null);
        boolean premium = pack != null && isPremiumPack(pack);
        List<PackDropRow> rows = packDropRowRepository.findByPackId(packId);
        if (rows.isEmpty()) {
            return rollFallbackUniform();
        }
        List<Long> ids = rows.stream().map(PackDropRow::getCardDefinitionId).toList();
        Map<Long, CardDefinition> defs =
                cardDefinitionRepository.findAllById(ids).stream()
                        .collect(Collectors.toMap(CardDefinition::getId, Function.identity()));
        List<CardDefinition> items = new ArrayList<>();
        List<Integer> weights = new ArrayList<>();
        for (PackDropRow r : rows) {
            CardDefinition d = defs.get(r.getCardDefinitionId());
            if (d != null) {
                items.add(d);
                weights.add(applyPremiumWeight(r.getWeight(), d.getRarity(), premium));
            }
        }
        if (items.isEmpty()) {
            return rollFallbackUniform();
        }
        return randomService.weightedPick(items, weights);
    }

    private static boolean isPremiumPack(Pack pack) {
        String n = pack.getName().toLowerCase();
        return n.contains("премиум") || n.contains("premium") || pack.getPriceCoins() >= 400;
    }

    private static int applyPremiumWeight(int base, CardRarity rarity, boolean premium) {
        if (!premium || rarity == CardRarity.COMMON) {
            return base;
        }
        return switch (rarity) {
            case RARE -> base * 3;
            case LEGENDARY -> base * 5;
            default -> base;
        };
    }

    private CardDefinition rollFallbackUniform() {
        List<CardDefinition> all = cardDefinitionRepository.findAll();
        if (all.isEmpty()) {
            throw new IllegalStateException("No cards defined in catalog");
        }
        List<Integer> weights = all.stream().map(c -> 1).toList();
        return randomService.weightedPick(all, weights);
    }
}
