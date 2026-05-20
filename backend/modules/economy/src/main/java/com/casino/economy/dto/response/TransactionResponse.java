package com.casino.economy.dto.response;

import com.casino.economy.entity.TransactionType;
import java.time.Instant;

public record TransactionResponse(
        long id, long amount, String operationId, TransactionType transactionType, String reason, Instant createdAt) {}
