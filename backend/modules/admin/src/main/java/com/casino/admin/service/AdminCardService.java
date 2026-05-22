package com.casino.admin.service;

import com.casino.admin.dto.request.CreateCardRequest;
import com.casino.cards.dto.response.CardDefinitionResponse;
import com.casino.cards.entity.CardDefinition;
import com.casino.cards.mapper.CardMapper;
import com.casino.cards.repository.CardDefinitionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminCardService {

    private final CardDefinitionRepository cardDefinitionRepository;
    private final CardMapper cardMapper;

    @Transactional(readOnly = true)
    public List<CardDefinitionResponse> list() {
        return cardDefinitionRepository.findAll().stream().map(cardMapper::toResponse).toList();
    }

    @Transactional
    public CardDefinitionResponse create(CreateCardRequest req) {
        String key = req.imageUrl() != null && !req.imageUrl().isBlank() ? req.imageUrl().trim() : null;
        CardDefinition c = cardDefinitionRepository.save(new CardDefinition(req.title(), req.rarity(), key));
        return cardMapper.toResponse(c);
    }
}
