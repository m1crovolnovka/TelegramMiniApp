package com.casino.economy.entity;

import com.casino.common.domain.base.BaseAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "wallets",
        uniqueConstraints = @UniqueConstraint(name = "uk_wallets_user", columnNames = "user_id"))
@Getter
@Setter
@NoArgsConstructor
public class Wallet extends BaseAuditableEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "balance", nullable = false)
    private long balance;

    public Wallet(Long userId, long balance) {
        this.userId = userId;
        this.balance = balance;
    }
}
