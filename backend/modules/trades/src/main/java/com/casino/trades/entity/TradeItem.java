package com.casino.trades.entity;

import com.casino.common.domain.base.BaseAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trade_items")
@Getter
@Setter
@NoArgsConstructor
public class TradeItem extends BaseAuditableEntity {

    @Column(name = "trade_id", nullable = false)
    private Long tradeId;

    @Column(name = "from_user_id", nullable = false)
    private Long fromUserId;

    @Column(name = "card_definition_id", nullable = false)
    private Long cardDefinitionId;

    @Column(nullable = false)
    private int quantity = 1;

    public TradeItem(Long tradeId, Long fromUserId, Long cardDefinitionId, int quantity) {
        this.tradeId = tradeId;
        this.fromUserId = fromUserId;
        this.cardDefinitionId = cardDefinitionId;
        this.quantity = quantity;
    }
}
