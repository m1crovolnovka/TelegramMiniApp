package com.casino.packs.entity;

import com.casino.cards.entity.CardRarity;
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
@Table(name = "pack_drop_rows")
@Getter
@Setter
@NoArgsConstructor
public class PackDropRow extends BaseAuditableEntity {

    @Column(name = "pack_id", nullable = false)
    private Long packId;

    @Column(name = "card_definition_id", nullable = false)
    private Long cardDefinitionId;

    @Column(nullable = false)
    private int weight = 1;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private CardRarity rarityHint;

    public PackDropRow(Long packId, Long cardDefinitionId, int weight, CardRarity rarityHint) {
        this.packId = packId;
        this.cardDefinitionId = cardDefinitionId;
        this.weight = weight;
        this.rarityHint = rarityHint;
    }
}
