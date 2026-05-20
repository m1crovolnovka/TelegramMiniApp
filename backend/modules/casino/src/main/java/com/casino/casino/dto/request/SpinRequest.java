package com.casino.casino.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SpinRequest(@NotNull @Min(1) Long bet) {}
