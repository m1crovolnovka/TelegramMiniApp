package com.casino.betting.entity;

import com.casino.common.domain.base.BaseAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_bets")
@Getter
@Setter
@NoArgsConstructor
public class UserBet extends BaseAuditableEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "betting_option_id", nullable = false)
    private Long bettingOptionId;

    @Column(name = "stake_coins", nullable = false)
    private long stakeCoins;

    @Column(name = "stake_operation_id", nullable = false, unique = true, length = 128)
    private String stakeOperationId;

    @Column(name = "paid_out", nullable = false)
    private boolean paidOut;

    public UserBet(Long userId, Long bettingOptionId, long stakeCoins, String stakeOperationId) {
        this.userId = userId;
        this.bettingOptionId = bettingOptionId;
        this.stakeCoins = stakeCoins;
        this.stakeOperationId = stakeOperationId;
    }
}
