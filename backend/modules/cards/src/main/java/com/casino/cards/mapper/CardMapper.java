package com.casino.cards.mapper;

import com.casino.cards.dto.response.CardDefinitionResponse;
import com.casino.cards.entity.CardDefinition;
import com.casino.cards.util.CardImageUrls;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {

    public CardDefinitionResponse toResponse(CardDefinition c) {
        String key = c.getImageStorageKey();
        return new CardDefinitionResponse(
                c.getId(),
                c.getTitle(),
                c.getRarity(),
                c.getTelegramUsername(),
                key,
                CardImageUrls.resolve(key));
    }
}
