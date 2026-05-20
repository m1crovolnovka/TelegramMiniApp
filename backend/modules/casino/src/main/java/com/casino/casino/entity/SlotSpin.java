package com.casino.casino.entity;

import com.casino.common.domain.base.BaseAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "casino_spin_logs")
@Getter
@Setter
@NoArgsConstructor
public class SlotSpin extends BaseAuditableEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "game_type", nullable = false, length = 32)
    private String gameType;

    @Column(name = "bet_amount", nullable = false)
    private long betAmount;

    @Column(name = "payout_amount", nullable = false)
    private long payoutAmount;

    public SlotSpin(Long userId, String gameType, long betAmount, long payoutAmount) {
        this.userId = userId;
        this.gameType = gameType;
        this.betAmount = betAmount;
        this.payoutAmount = payoutAmount;
    }
}
