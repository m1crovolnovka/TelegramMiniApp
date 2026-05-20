package com.casino.cards.dto.response;

import com.casino.cards.entity.CardRarity;

public record CollectionProgressResponse(
        long ownedDefinitions, long totalDefinitions, double percentByDefinitions, CardRarityBreakdown byRarity) {

    public record CardRarityBreakdown(long commonOwned, long rareOwned, long legendaryOwned) {}
}
