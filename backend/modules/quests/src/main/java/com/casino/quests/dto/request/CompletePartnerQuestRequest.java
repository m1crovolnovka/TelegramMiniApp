package com.casino.quests.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CompletePartnerQuestRequest(
        @NotBlank String externalAssignmentId,
        @NotBlank String partnerAUsername,
        @NotBlank String partnerBUsername,
        @NotNull @Min(0) Long rewardCoins) {}
