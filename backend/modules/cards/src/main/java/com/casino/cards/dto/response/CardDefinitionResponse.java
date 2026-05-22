package com.casino.cards.dto.response;

import com.casino.cards.entity.CardRarity;

public record CardDefinitionResponse(
        long id, String title, CardRarity rarity, String imageStorageKey, String imageUrl) {}
