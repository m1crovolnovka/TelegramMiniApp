package com.casino.packs.service;

import com.casino.cards.entity.CardDefinition;
import com.casino.cards.entity.CardRarity;
import com.casino.cards.repository.CardDefinitionRepository;
import com.casino.packs.entity.Pack;
import com.casino.packs.entity.PackBundleSlot;
import com.casino.packs.entity.PackDropRow;
import com.casino.packs.entity.PackKind;
import com.casino.packs.repository.PackBundleSlotRepository;
import com.casino.packs.repository.PackDropRowRepository;
import com.casino.packs.repository.PackRepository;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DropCalculationService {

    private final PackDropRowRepository packDropRowRepository;
    private final PackBundleSlotRepository packBundleSlotRepository;
    private final PackRepository packRepository;
    private final CardDefinitionRepository cardDefinitionRepository;
    private final RandomService randomService;

    public CardDefinition rollCard(long packId) {
        return rollCardOfRarity(packId, null);
    }

    public List<CardDefinition> rollBundle(long packId) {
        List<PackBundleSlot> slots = packBundleSlotRepository.findByPackIdOrderByIdAsc(packId);
        if (slots.isEmpty()) {
            throw new IllegalStateException("Bundle pack has no slots: " + packId);
        }
        List<CardDefinition> result = new ArrayList<>();
        for (PackBundleSlot slot : slots) {
            for (int i = 0; i < slot.getQuantity(); i++) {
                result.add(rollCardOfRarity(packId, slot.getRarity()));
            }
        }
        return result;
    }

    /**
     * Picks a rarity tier by summed row weights, then a uniform random card within that tier.
     * This avoids bias when multiple cards share the same per-rarity weight.
     */
    public CardDefinition rollCardOfRarity(long packId, CardRarity forcedRarity) {
        Pack pack = packRepository.findById(packId).orElse(null);
        boolean premium = pack != null && isPremiumPack(pack);
        List<PackDropRow> rows = packDropRowRepository.findByPackId(packId);
        if (rows.isEmpty()) {
            return rollFallbackUniform(forcedRarity);
        }
        List<Long> ids = rows.stream().map(PackDropRow::getCardDefinitionId).toList();
        Map<Long, CardDefinition> defs =
                cardDefinitionRepository.findAllById(ids).stream()
                        .collect(Collectors.toMap(CardDefinition::getId, Function.identity()));

        Map<CardRarity, List<CardDefinition>> byRarity = new EnumMap<>(CardRarity.class);
        Map<CardRarity, Integer> tierWeights = new EnumMap<>(CardRarity.class);
        for (PackDropRow row : rows) {
            CardDefinition card = defs.get(row.getCardDefinitionId());
            if (card == null) {
                continue;
            }
            CardRarity rarity = card.getRarity();
            byRarity.computeIfAbsent(rarity, r -> new ArrayList<>()).add(card);
            int w = applyPremiumWeight(row.getWeight(), rarity, premium);
            tierWeights.merge(rarity, w, Integer::sum);
        }
        if (byRarity.isEmpty()) {
            return rollFallbackUniform(forcedRarity);
        }

        CardRarity chosenRarity = forcedRarity;
        if (chosenRarity == null) {
            List<CardRarity> rarities = new ArrayList<>(tierWeights.keySet());
            List<Integer> weights = rarities.stream().map(tierWeights::get).toList();
            chosenRarity = randomService.weightedPick(rarities, weights);
        }

        List<CardDefinition> pool = byRarity.get(chosenRarity);
        if (pool == null || pool.isEmpty()) {
            return rollFallbackUniform(forcedRarity);
        }
        List<Integer> uniform = pool.stream().map(c -> 1).toList();
        return randomService.weightedPick(pool, uniform);
    }

    @Transactional
    public void syncDropRowsForSinglePacks() {
        List<CardDefinition> catalog = cardDefinitionRepository.findAll();
        if (catalog.isEmpty()) {
            return;
        }
        for (Pack pack : packRepository.findAll()) {
            if (pack.getPackKind() == PackKind.BUNDLE) {
                continue;
            }
            boolean premium = isPremiumPack(pack);
            Set<Long> existing =
                    packDropRowRepository.findByPackId(pack.getId()).stream()
                            .map(PackDropRow::getCardDefinitionId)
                            .collect(Collectors.toCollection(HashSet::new));
            for (CardDefinition card : catalog) {
                if (!existing.contains(card.getId())) {
                    int w = premium ? premiumWeight(card.getRarity()) : starterWeight(card.getRarity());
                    packDropRowRepository.save(
                            new PackDropRow(pack.getId(), card.getId(), w, card.getRarity()));
                }
            }
        }
    }

    private static boolean isPremiumPack(Pack pack) {
        String n = pack.getName().toLowerCase();
        return n.contains("премиум") || n.contains("premium") || pack.getPriceCoins() >= 400;
    }

    private static int applyPremiumWeight(int base, CardRarity rarity, boolean premium) {
        if (!premium) {
            return base;
        }
        return switch (rarity) {
            case COMMON -> base;
            case RARE -> (int) Math.round(base * 1.35);
            case LEGENDARY -> base * 2;
        };
    }

    private static int starterWeight(CardRarity rarity) {
        return switch (rarity) {
            case COMMON -> 100;
            case RARE -> 10;
            case LEGENDARY -> 2;
        };
    }

    private static int premiumWeight(CardRarity rarity) {
        return switch (rarity) {
            case COMMON -> 42;
            case RARE -> 22;
            case LEGENDARY -> 10;
        };
    }

    private CardDefinition rollFallbackUniform(CardRarity forcedRarity) {
        List<CardDefinition> all = cardDefinitionRepository.findAll();
        if (all.isEmpty()) {
            throw new IllegalStateException("No cards defined in catalog");
        }
        List<CardDefinition> pool =
                forcedRarity == null
                        ? all
                        : all.stream().filter(c -> c.getRarity() == forcedRarity).toList();
        if (pool.isEmpty()) {
            pool = all;
        }
        List<Integer> weights = pool.stream().map(c -> 1).toList();
        return randomService.weightedPick(pool, weights);
    }
}
