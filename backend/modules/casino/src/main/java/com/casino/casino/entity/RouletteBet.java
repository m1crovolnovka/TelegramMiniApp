package com.casino.casino.entity;

import com.casino.common.domain.base.BaseAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roulette_bets")
@Getter
@Setter
@NoArgsConstructor
public class RouletteBet extends BaseAuditableEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "bet_type", nullable = false, length = 32)
    private RouletteBetType betType;

    @Column(name = "number_value")
    private Integer numberValue;

    @Column(name = "stake_coins", nullable = false)
    private long stakeCoins;

    @Column(name = "payout_coins", nullable = false)
    private long payoutCoins;

    @Column(name = "rolled_value", nullable = false)
    private int rolledValue;

    public RouletteBet(
            Long userId,
            RouletteBetType betType,
            Integer numberValue,
            long stakeCoins,
            long payoutCoins,
            int rolledValue) {
        this.userId = userId;
        this.betType = betType;
        this.numberValue = numberValue;
        this.stakeCoins = stakeCoins;
        this.payoutCoins = payoutCoins;
        this.rolledValue = rolledValue;
    }
}
