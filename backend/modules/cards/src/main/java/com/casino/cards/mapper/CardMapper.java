package com.casino.cards.mapper;

import com.casino.cards.dto.response.CardDefinitionResponse;
import com.casino.cards.entity.CardDefinition;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {

    public CardDefinitionResponse toResponse(CardDefinition c) {
        return new CardDefinitionResponse(c.getId(), c.getTitle(), c.getRarity(), c.getImageStorageKey());
    }
}
