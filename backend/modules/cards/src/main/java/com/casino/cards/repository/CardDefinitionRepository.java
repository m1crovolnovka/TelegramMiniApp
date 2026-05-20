package com.casino.cards.repository;

import com.casino.cards.entity.CardDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardDefinitionRepository extends JpaRepository<CardDefinition, Long> {}
