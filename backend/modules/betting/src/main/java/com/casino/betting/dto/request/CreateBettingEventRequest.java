package com.casino.betting.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record CreateBettingEventRequest(@NotBlank String title, @NotEmpty List<String> optionLabels) {}
