package com.casino.quests.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubmitQuestRequest(@NotNull Long questId, @NotBlank String proofText) {}
