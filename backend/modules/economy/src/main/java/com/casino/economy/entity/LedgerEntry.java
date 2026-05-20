package com.casino.economy.entity;

import com.casino.common.domain.base.BaseAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "ledger_entries",
        uniqueConstraints =
                @UniqueConstraint(name = "uk_ledger_user_operation", columnNames = {"user_id", "operation_id"}))
@Getter
@Setter
@NoArgsConstructor
public class LedgerEntry extends BaseAuditableEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "amount", nullable = false)
    private long amount;

    @Column(name = "operation_id", nullable = false, length = 128)
    private String operationId;

    @Column(name = "reason", nullable = false, length = 256)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 32)
    private TransactionType transactionType = TransactionType.OTHER;

    public LedgerEntry(
            Long userId, long amount, String operationId, TransactionType transactionType, String reason) {
        this.userId = userId;
        this.amount = amount;
        this.operationId = operationId;
        this.transactionType = transactionType;
        this.reason = reason;
    }
}
