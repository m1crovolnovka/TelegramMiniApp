package com.casino.economy.mapper;

import com.casino.economy.dto.response.TransactionResponse;
import com.casino.economy.entity.LedgerEntry;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionResponse toResponse(LedgerEntry e) {
        return new TransactionResponse(
                e.getId(),
                e.getAmount(),
                e.getOperationId(),
                e.getTransactionType(),
                e.getReason(),
                e.getCreatedAt());
    }
}
