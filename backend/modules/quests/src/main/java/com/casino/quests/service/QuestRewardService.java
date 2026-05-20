package com.casino.quests.service;

import com.casino.economy.api.EconomyPort;
import com.casino.economy.entity.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestRewardService {

    private final EconomyPort economyPort;

    public void payReward(long userId, long amountCoins, long submissionId) {
        economyPort.credit(
                userId,
                amountCoins,
                "quest-reward:" + submissionId,
                TransactionType.QUEST_REWARD,
                "quest_reward");
    }
}
