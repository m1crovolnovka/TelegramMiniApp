package com.casino.cards.entity;

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
        name = "user_cards",
        uniqueConstraints =
                @UniqueConstraint(name = "uk_user_card", columnNames = {"user_id", "card_definition_id"}))
@Getter
@Setter
@NoArgsConstructor
public class UserCard extends BaseAuditableEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "card_definition_id", nullable = false)
    private Long cardDefinitionId;

    @Column(nullable = false)
    private int quantity = 1;

    @Column(name = "locked", nullable = false)
    private boolean locked = false;

    @Column(name = "locked_trade_id")
    private Long lockedTradeId;

    public UserCard(Long userId, Long cardDefinitionId, int quantity) {
        this.userId = userId;
        this.cardDefinitionId = cardDefinitionId;
        this.quantity = quantity;
    }
}
