package com.casino.cards.dto.response;

import com.casino.cards.entity.CardRarity;

public record InventoryItemResponse(
        long cardDefinitionId, String title, CardRarity rarity, int quantity, boolean locked, Long lockedTradeId) {}
