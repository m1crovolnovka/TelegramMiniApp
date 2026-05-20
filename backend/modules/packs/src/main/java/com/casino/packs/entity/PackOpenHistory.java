package com.casino.packs.entity;

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
        name = "pack_open_history",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "idempotency_key"}))
@Getter
@Setter
@NoArgsConstructor
public class PackOpenHistory extends BaseAuditableEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "pack_id", nullable = false)
    private Long packId;

    @Column(name = "dropped_card_definition_id", nullable = false)
    private Long droppedCardDefinitionId;

    @Column(name = "idempotency_key", length = 128)
    private String idempotencyKey;

    public PackOpenHistory(
            Long userId, Long packId, Long droppedCardDefinitionId, String idempotencyKey) {
        this.userId = userId;
        this.packId = packId;
        this.droppedCardDefinitionId = droppedCardDefinitionId;
        this.idempotencyKey = idempotencyKey;
    }
}
