package com.casino.casino.dto.request;

import com.casino.casino.entity.RouletteBetType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RouletteBetRequest(RouletteBetType betType, Integer numberValue, @NotNull @Min(1) Long stake) {}
