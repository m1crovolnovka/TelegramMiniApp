package com.casino.quests.dto.response;

public record PartnerQuestCompleteResponse(
        boolean alreadyProcessed,
        int completedTogetherCount,
        boolean grantedRareMilestone,
        boolean grantedLegendaryMilestone) {}
