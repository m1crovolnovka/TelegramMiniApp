package com.casino.cards.dto.response;

import com.casino.cards.entity.CardRarity;

public record CollectionProgressResponse(
        long ownedDefinitions,
        long totalDefinitions,
        double percentByDefinitions,
        long ownedUniqueStudents,
        long totalUniqueStudents,
        double percentUniqueStudents,
        CardRarityBreakdown byRarity) {

    public record CardRarityBreakdown(long commonOwned, long rareOwned, long legendaryOwned) {}
}
