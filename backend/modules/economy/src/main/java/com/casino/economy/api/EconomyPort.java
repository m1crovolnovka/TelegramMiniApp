package com.casino.economy.api;

import com.casino.economy.entity.TransactionType;

public interface EconomyPort {

    void ensureWallet(long userId);

    long getBalance(long userId);

    void debit(long userId, long amount, String operationId, TransactionType type, String reason);

    void credit(long userId, long amount, String operationId, TransactionType type, String reason);

    /**
     * Atomic transfer: debits {@code fromUserId} and credits {@code toUserId}. Uses paired operation ids for
     * idempotency.
     */
    void transfer(long fromUserId, long toUserId, long amount, String transferOperationId, String reason);
}
