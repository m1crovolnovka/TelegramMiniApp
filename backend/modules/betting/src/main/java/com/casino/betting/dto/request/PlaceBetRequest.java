package com.casino.betting.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PlaceBetRequest(@NotNull Long optionId, @NotNull @Min(1) Long stakeCoins) {}
