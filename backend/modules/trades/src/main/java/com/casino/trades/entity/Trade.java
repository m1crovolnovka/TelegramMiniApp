package com.casino.trades.entity;

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
@Table(name = "trades")
@Getter
@Setter
@NoArgsConstructor
public class Trade extends BaseAuditableEntity {

    @Column(name = "initiator_user_id", nullable = false)
    private Long initiatorUserId;

    @Column(name = "partner_user_id", nullable = false)
    private Long partnerUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private TradeStatus status = TradeStatus.DRAFT;

    public Trade(Long initiatorUserId, Long partnerUserId, TradeStatus status) {
        this.initiatorUserId = initiatorUserId;
        this.partnerUserId = partnerUserId;
        this.status = status;
    }
}
