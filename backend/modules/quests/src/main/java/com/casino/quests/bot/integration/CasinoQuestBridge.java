package com.casino.quests.bot.integration;

import com.casino.quests.dto.request.CompletePartnerQuestRequest;
import com.casino.quests.service.PartnerQuestRewardService;
import com.casino.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CasinoQuestBridge {

    private final UserService casinoUserService;
    private final PartnerQuestRewardService partnerQuestRewardService;

    public void ensureCasinoUser(String username, long telegramChatId) {
        casinoUserService.findOrCreateByUsername(username, telegramChatId);
    }

    public void completePartnerQuest(
            String externalAssignmentId, String partnerA, String partnerB, long rewardCoins) {
        partnerQuestRewardService.complete(
                new CompletePartnerQuestRequest(externalAssignmentId, partnerA, partnerB, rewardCoins));
    }
}
