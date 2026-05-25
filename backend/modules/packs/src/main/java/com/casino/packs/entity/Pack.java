package com.casino.packs.entity;

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
@Table(name = "pack_definitions")
@Getter
@Setter
@NoArgsConstructor
public class Pack extends BaseAuditableEntity {

    @Column(nullable = false, length = 128)
    private String name;

    @Column(name = "price_coins", nullable = false)
    private long priceCoins;

    @Enumerated(EnumType.STRING)
    @Column(name = "pack_kind", nullable = false, length = 16)
    private PackKind packKind = PackKind.SINGLE;

    public Pack(String name, long priceCoins) {
        this.name = name;
        this.priceCoins = priceCoins;
    }

    public Pack(String name, long priceCoins, PackKind packKind) {
        this.name = name;
        this.priceCoins = priceCoins;
        this.packKind = packKind;
    }
}
