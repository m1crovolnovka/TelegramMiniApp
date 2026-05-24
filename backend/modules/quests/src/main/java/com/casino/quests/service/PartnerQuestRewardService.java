package com.casino.quests.service;

import com.casino.cards.api.InventoryPort;
import com.casino.cards.entity.CardDefinition;
import com.casino.cards.entity.CardRarity;
import com.casino.cards.repository.CardDefinitionRepository;
import com.casino.common.exception.BusinessException;
import com.casino.common.exception.ErrorCode;
import com.casino.economy.api.EconomyPort;
import com.casino.economy.entity.TransactionType;
import com.casino.quests.dto.request.CompletePartnerQuestRequest;
import com.casino.quests.dto.response.PartnerQuestCompleteResponse;
import com.casino.quests.entity.PartnerQuestCompletion;
import com.casino.quests.entity.PartnerQuestProgress;
import com.casino.quests.repository.PartnerQuestCompletionRepository;
import com.casino.quests.repository.PartnerQuestProgressRepository;
import com.casino.users.entity.User;
import com.casino.users.service.UserService;
import com.casino.users.util.UsernameNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartnerQuestRewardService {

    private static final int RARE_MILESTONE = 7;
    private static final int LEGENDARY_MILESTONE = 15;

    private final PartnerQuestCompletionRepository completionRepository;
    private final PartnerQuestProgressRepository progressRepository;
    private final UserService userService;
    private final EconomyPort economyPort;
    private final InventoryPort inventoryPort;
    private final CardDefinitionRepository cardDefinitionRepository;

    @Transactional
    public PartnerQuestCompleteResponse complete(CompletePartnerQuestRequest req) {
        if (completionRepository.existsByExternalAssignmentId(req.externalAssignmentId())) {
            long[] pair = orderedUserIds(req.partnerAUsername(), req.partnerBUsername());
            int count =
                    progressRepository
                            .findByUserLowIdAndUserHighId(pair[0], pair[1])
                            .map(PartnerQuestProgress::getCompletedCount)
                            .orElse(0);
            return new PartnerQuestCompleteResponse(true, count, false, false);
        }

        User userA = userService.requireByUsername(req.partnerAUsername());
        User userB = userService.requireByUsername(req.partnerBUsername());
        if (userA.getId().equals(userB.getId())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Partners must be different users");
        }

        long low = Math.min(userA.getId(), userB.getId());
        long high = Math.max(userA.getId(), userB.getId());
        completionRepository.save(new PartnerQuestCompletion(req.externalAssignmentId(), low, high));

        PartnerQuestProgress progress =
                progressRepository
                        .findByUserLowIdAndUserHighId(low, high)
                        .orElseGet(() -> progressRepository.save(new PartnerQuestProgress(low, high)));
        progress.setCompletedCount(progress.getCompletedCount() + 1);
        progressRepository.save(progress);

        long reward = req.rewardCoins();
        if (reward > 0) {
            String debitKey = "partner-quest:" + req.externalAssignmentId();
            economyPort.credit(
                    userA.getId(), reward, debitKey + ":a", TransactionType.QUEST_REWARD, "partner_quest");
            economyPort.credit(
                    userB.getId(), reward, debitKey + ":b", TransactionType.QUEST_REWARD, "partner_quest");
        }

        grantPartnerCard(userA, userB, CardRarity.COMMON);

        boolean rare = progress.getCompletedCount() == RARE_MILESTONE;
        boolean legendary = progress.getCompletedCount() == LEGENDARY_MILESTONE;
        if (rare) {
            grantPartnerCard(userA, userB, CardRarity.RARE);
        }
        if (legendary) {
            grantPartnerCard(userA, userB, CardRarity.LEGENDARY);
        }

        return new PartnerQuestCompleteResponse(
                false, progress.getCompletedCount(), rare, legendary);
    }

    private void grantPartnerCard(User owner, User partner, CardRarity rarity) {
        String partnerUsername = UsernameNormalizer.normalize(partner.getUsername());
        cardDefinitionRepository
                .findByTelegramUsernameIgnoreCaseAndRarity(partnerUsername, rarity)
                .ifPresent(card -> inventoryPort.addCard(owner.getId(), card.getId(), 1));
        String ownerUsername = UsernameNormalizer.normalize(owner.getUsername());
        cardDefinitionRepository
                .findByTelegramUsernameIgnoreCaseAndRarity(ownerUsername, rarity)
                .ifPresent(card -> inventoryPort.addCard(partner.getId(), card.getId(), 1));
    }

    private long[] orderedUserIds(String usernameA, String usernameB) {
        User a = userService.requireByUsername(usernameA);
        User b = userService.requireByUsername(usernameB);
        long low = Math.min(a.getId(), b.getId());
        long high = Math.max(a.getId(), b.getId());
        return new long[] {low, high};
    }
}
