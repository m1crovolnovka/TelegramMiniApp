package com.casino.cards.api;

/**
 * Public API for other modules (packs, trades) to mutate inventory without reaching into repositories.
 */
public interface InventoryPort {

    void addCard(long userId, long cardDefinitionId, int quantity);

    void removeCard(long userId, long cardDefinitionId, int quantity);

    void setLockedForTrade(long userId, long cardDefinitionId, boolean locked, Long tradeId);
}
