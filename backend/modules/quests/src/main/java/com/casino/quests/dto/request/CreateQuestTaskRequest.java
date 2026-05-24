package com.casino.quests.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateQuestTaskRequest(@NotBlank String description, @Min(0) long rewardCoins) {}
