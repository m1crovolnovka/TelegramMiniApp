package com.casino.cards.entity;

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
@Table(name = "card_definitions")
@Getter
@Setter
@NoArgsConstructor
public class CardDefinition extends BaseAuditableEntity {

    @Column(nullable = false, length = 128)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardRarity rarity = CardRarity.COMMON;

    @Column(name = "image_storage_key", length = 512)
    private String imageStorageKey;

    @Column(name = "telegram_username", length = 64)
    private String telegramUsername;

    public CardDefinition(String title, CardRarity rarity, String imageStorageKey) {
        this.title = title;
        this.rarity = rarity;
        this.imageStorageKey = imageStorageKey;
    }
}
