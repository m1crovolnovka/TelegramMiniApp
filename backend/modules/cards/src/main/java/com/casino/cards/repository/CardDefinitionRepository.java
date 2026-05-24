package com.casino.cards.repository;

import com.casino.cards.entity.CardDefinition;
import com.casino.cards.entity.CardRarity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardDefinitionRepository extends JpaRepository<CardDefinition, Long> {

    Optional<CardDefinition> findByTelegramUsernameIgnoreCaseAndRarity(
            String telegramUsername, CardRarity rarity);
}
