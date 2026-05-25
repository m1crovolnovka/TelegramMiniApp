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
@Table(name = "pack_bundle_slots")
@Getter
@Setter
@NoArgsConstructor
public class PackBundleSlot extends BaseAuditableEntity {

    @Column(name = "pack_id", nullable = false)
    private Long packId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private CardRarity rarity;

    @Column(nullable = false)
    private int quantity = 1;

    public PackBundleSlot(Long packId, CardRarity rarity, int quantity) {
        this.packId = packId;
        this.rarity = rarity;
        this.quantity = quantity;
    }
}
