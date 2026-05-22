package com.casino.app.config;

import com.casino.betting.dto.request.CreateBettingEventRequest;
import com.casino.betting.service.BetSettlementService;
import com.casino.cards.entity.CardDefinition;
import com.casino.cards.entity.CardRarity;
import com.casino.cards.repository.CardDefinitionRepository;
import com.casino.economy.api.EconomyPort;
import com.casino.economy.entity.TransactionType;
import com.casino.packs.entity.Pack;
import com.casino.packs.entity.PackDropRow;
import com.casino.packs.repository.PackDropRowRepository;
import com.casino.packs.repository.PackRepository;
import com.casino.quests.dto.request.CreateQuestRequest;
import com.casino.quests.service.QuestManagementService;
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

    public static final String ADMIN_INIT_HINT = "dev-admin-login-token-2026";

    private final CardDefinitionRepository cardDefinitionRepository;
    private final PackRepository packRepository;
    private final PackDropRowRepository packDropRowRepository;
    private final QuestManagementService questManagementService;
    private final BetSettlementService betSettlementService;
    private final UserRepository userRepository;
    private final EconomyPort economyPort;
    private final TelegramInitDataValidator telegramInitDataValidator;

    @EventListener(ApplicationReadyEvent.class)
    @Order(100)
    @Transactional
    public void seed() {
        if (cardDefinitionRepository.count() > 0) {
            ensureAdminUser();
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
            int weight =
                    switch (c.getRarity()) {
                        case COMMON -> 50;
                        case RARE -> 15;
                        case LEGENDARY -> 3;
                    };
            packDropRowRepository.save(new PackDropRow(starter.getId(), c.getId(), weight, c.getRarity()));
            packDropRowRepository.save(new PackDropRow(premium.getId(), c.getId(), weight * 2, c.getRarity()));
        }

        questManagementService.createQuest(new CreateQuestRequest("Подпишись на канал", 200));
        questManagementService.createQuest(new CreateQuestRequest("Пригласи друга", 500));

        betSettlementService.createEvent(
                new CreateBettingEventRequest("Кто победит в турнире?", List.of("Команда A", "Команда B")));

        ensureAdminUser();
        log.info("Dev seed complete. Admin initData hint: {}", ADMIN_INIT_HINT);
    }

    private CardDefinition card(String title, CardRarity rarity) {
        String slug = title.toLowerCase().replace(" ", "-");
        String imageUrl =
                "https://api.dicebear.com/7.x/avataaars/png?seed=" + slug + "&backgroundColor=b6e3f4";
        return new CardDefinition(title, rarity, imageUrl);
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
                        .findByTelegramId(adminTelegramId)
                        .orElseGet(
                                () ->
                                        userRepository.save(
                                                new User(adminTelegramId, "admin", UserRole.ADMIN)));
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
