package com.casino.quests.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateQuestRequest(@NotBlank String title, @PositiveOrZero long rewardCoins) {}
