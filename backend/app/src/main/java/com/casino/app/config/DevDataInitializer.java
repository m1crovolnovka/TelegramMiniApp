package com.casino.app.config;

import com.casino.betting.dto.request.CreateBettingEventRequest;
import com.casino.betting.service.BetSettlementService;
import com.casino.cards.entity.CardDefinition;
import com.casino.cards.entity.CardRarity;
import com.casino.cards.repository.CardDefinitionRepository;
import com.casino.economy.api.EconomyPort;
import com.casino.economy.entity.TransactionType;
import com.casino.packs.entity.Pack;
import com.casino.packs.entity.PackBundleSlot;
import com.casino.packs.entity.PackDropRow;
import com.casino.packs.entity.PackKind;
import com.casino.packs.repository.PackBundleSlotRepository;
import com.casino.packs.repository.PackDropRowRepository;
import com.casino.packs.repository.PackRepository;
import com.casino.packs.service.DropCalculationService;
import com.casino.quests.bot.entity.QuestTaskEntity;
import com.casino.quests.bot.repo.QuestTaskRepository;
import com.casino.auth.integration.telegram.TelegramInitDataValidator;
import com.casino.users.entity.User;
import com.casino.users.entity.UserRole;
import com.casino.users.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seeds demo content for local development. Disable with {@code casino.dev.seed=false}.
 */
@Component
@ConditionalOnProperty(name = "casino.dev.seed", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class DevDataInitializer {

    public static final String ADMIN_INIT_HINT = "dev-devadmin-login-token-2026";

    private final CardDefinitionRepository cardDefinitionRepository;
    private final PackRepository packRepository;
    private final PackDropRowRepository packDropRowRepository;
    private final PackBundleSlotRepository packBundleSlotRepository;
    private final DropCalculationService dropCalculationService;
    private final QuestTaskRepository questTaskRepository;
    private final BetSettlementService betSettlementService;
    private final UserRepository userRepository;
    private final EconomyPort economyPort;
    private final TelegramInitDataValidator telegramInitDataValidator;

    @EventListener(ApplicationReadyEvent.class)
    @Order(100)
    @Transactional
    public void seed() {
        if (cardDefinitionRepository.count() > 0) {
            purgeStubUsers();
            ensureAdminUser();
            ensureQuestTasks();
            ensurePackDropWeights();
            dropCalculationService.syncDropRowsForSinglePacks();
            ensureBundlePacks();
            return;
        }
        log.info("Seeding dev data…");

        List<CardDefinition> cards =
                List.of(
                        card("Алексей", CardRarity.COMMON),
                        card("Мария", CardRarity.COMMON),
                        card("Дмитрий", CardRarity.COMMON),
                        card("Елена", CardRarity.RARE),
                        card("Вячеслав", CardRarity.RARE),
                        card("София", CardRarity.RARE),
                        card("Никита", CardRarity.LEGENDARY),
                        card("Анна", CardRarity.LEGENDARY));
        cardDefinitionRepository.saveAll(cards);

        Pack starter = packRepository.save(new Pack("Стартовый пак", 100));
        Pack premium = packRepository.save(new Pack("Премиум пак", 500));
        for (CardDefinition c : cards) {
            packDropRowRepository.save(
                    new PackDropRow(starter.getId(), c.getId(), starterWeight(c.getRarity()), c.getRarity()));
            packDropRowRepository.save(
                    new PackDropRow(premium.getId(), c.getId(), premiumWeight(c.getRarity()), c.getRarity()));
        }

        ensureBundlePacks();
        ensureQuestTasks();

        betSettlementService.createEvent(
                new CreateBettingEventRequest("Кто победит в турнире?", List.of("Команда A", "Команда B")));

        ensureAdminUser();
        log.info("Dev seed complete. Admin initData hint: {}", ADMIN_INIT_HINT);
    }

    private void ensureBundlePacks() {
        seedBundlePack("Набор «Микс»", 2800, List.of(
                new SlotDef(CardRarity.COMMON, 4),
                new SlotDef(CardRarity.RARE, 2),
                new SlotDef(CardRarity.LEGENDARY, 1)));
        seedBundlePack("Набор «Обычный»", 1200, List.of(new SlotDef(CardRarity.COMMON, 5)));
        seedBundlePack("Набор «Редкий»", 1700, List.of(new SlotDef(CardRarity.RARE, 3)));
        ensureBundleDropPools();
    }

    private void seedBundlePack(String name, long price, List<SlotDef> slots) {
        Pack existing =
                packRepository.findAll().stream().filter(p -> name.equals(p.getName())).findFirst().orElse(null);
        Pack pack;
        if (existing == null) {
            pack = packRepository.save(new Pack(name, price, PackKind.BUNDLE));
        } else {
            pack = existing;
            pack.setPackKind(PackKind.BUNDLE);
            pack.setPriceCoins(price);
            packRepository.save(pack);
            packBundleSlotRepository.deleteByPackId(pack.getId());
        }
        for (SlotDef slot : slots) {
            packBundleSlotRepository.save(new PackBundleSlot(pack.getId(), slot.rarity(), slot.count()));
        }
    }

    /** Bundle rolls use the same rarity pools as single packs — ensure all single packs have full catalog rows. */
    private void ensureBundleDropPools() {
        dropCalculationService.syncDropRowsForSinglePacks();
    }

    private record SlotDef(CardRarity rarity, int count) {}

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

    private void ensurePackDropWeights() {
        packRepository.findAll().forEach(pack -> {
            if (pack.getPackKind() == PackKind.BUNDLE) {
                return;
            }
            boolean premium =
                    pack.getName().toLowerCase().contains("премиум")
                            || pack.getName().toLowerCase().contains("premium")
                            || pack.getPriceCoins() >= 400;
            for (PackDropRow row : packDropRowRepository.findByPackId(pack.getId())) {
                cardDefinitionRepository
                        .findById(row.getCardDefinitionId())
                        .ifPresent(
                                card -> {
                                    int w =
                                            premium
                                                    ? premiumWeight(card.getRarity())
                                                    : starterWeight(card.getRarity());
                                    if (row.getWeight() != w) {
                                        row.setWeight(w);
                                        packDropRowRepository.save(row);
                                    }
                                });
            }
        });
    }

    private void ensureQuestTasks() {
        if (questTaskRepository.count() > 0) {
            return;
        }
        questTaskRepository.save(new QuestTaskEntity("Подпишись на канал", 200));
        questTaskRepository.save(new QuestTaskEntity("Сделай селфи с партнёром", 300));
        questTaskRepository.save(new QuestTaskEntity("Пригласи друга в бота", 500));
        log.info("Seeded {} quest tasks for bot", questTaskRepository.count());
    }

    private CardDefinition card(String title, CardRarity rarity) {
        String slug = title.toLowerCase().replace(" ", "-");
        String imageUrl =
                "https://api.dicebear.com/7.x/avataaars/png?seed=" + slug + "&backgroundColor=b6e3f4";
        CardDefinition c = new CardDefinition(title, rarity, imageUrl);
        c.setTelegramUsername(slug);
        return c;
    }

    private void purgeStubUsers() {
        userRepository.findByUsernameIgnoreCaseIn(List.of("admin", "player")).forEach(userRepository::delete);
        log.info("Removed stub users admin/player if present");
    }

    private void ensureAdminUser() {
        long adminTelegramId;
        try {
            adminTelegramId = telegramInitDataValidator.deriveStubTelegramUserId(ADMIN_INIT_HINT);
        } catch (Exception e) {
            log.warn("Cannot derive admin telegram id: {}", e.getMessage());
            return;
        }
        User admin =
                userRepository
                        .findByUsernameIgnoreCase("devadmin")
                        .orElseGet(
                                () ->
                                        userRepository.save(
                                                new User(adminTelegramId, "devadmin", UserRole.ADMIN)));
        if (admin.getRole() != UserRole.ADMIN) {
            admin.setRole(UserRole.ADMIN);
        }
        economyPort.ensureWallet(admin.getId());
        economyPort.credit(
                admin.getId(),
                50_000,
                "dev:admin:starter",
                TransactionType.ADMIN_ADJUST,
                "dev_seed");
    }
}
