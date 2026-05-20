package com.casino.quests.dto.response;

import com.casino.quests.entity.QuestStatus;

public record QuestResponse(long id, String title, long rewardCoins, QuestStatus status) {}
