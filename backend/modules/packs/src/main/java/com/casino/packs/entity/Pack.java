package com.casino.packs.entity;

import com.casino.common.domain.base.BaseAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    public Pack(String name, long priceCoins) {
        this.name = name;
        this.priceCoins = priceCoins;
    }
}
