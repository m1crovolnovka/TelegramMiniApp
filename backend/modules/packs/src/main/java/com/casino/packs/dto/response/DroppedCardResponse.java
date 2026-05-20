package com.casino.packs.dto.response;

import com.casino.cards.entity.CardRarity;

public record DroppedCardResponse(long cardDefinitionId, String title, CardRarity rarity) {}
