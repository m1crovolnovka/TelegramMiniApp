package com.casino.betting.entity;

import com.casino.common.domain.base.BaseAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "betting_options")
@Getter
@Setter
@NoArgsConstructor
public class BettingOption extends BaseAuditableEntity {

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(nullable = false, length = 128)
    private String label;

    @Column(name = "total_stake_coins", nullable = false)
    private long totalStakeCoins;

    @Column(name = "winning", nullable = false)
    private boolean winning;

    public BettingOption(Long eventId, String label) {
        this.eventId = eventId;
        this.label = label;
    }
}
