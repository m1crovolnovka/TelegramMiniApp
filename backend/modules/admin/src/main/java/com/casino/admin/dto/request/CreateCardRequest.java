package com.casino.admin.dto.request;

import com.casino.cards.entity.CardRarity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCardRequest(
        @NotBlank String title, @NotNull CardRarity rarity, String imageUrl) {}
