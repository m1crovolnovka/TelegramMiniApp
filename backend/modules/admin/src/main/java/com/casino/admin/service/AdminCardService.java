package com.casino.admin.service;

import com.casino.admin.dto.request.CreateCardRequest;
import com.casino.admin.dto.request.UpdateCardRequest;
import com.casino.cards.dto.response.CardDefinitionResponse;
import com.casino.cards.entity.CardDefinition;
import com.casino.cards.mapper.CardMapper;
import com.casino.cards.repository.CardDefinitionRepository;
import com.casino.common.exception.BusinessException;
import com.casino.common.exception.ErrorCode;
import com.casino.users.util.UsernameNormalizer;
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
        CardDefinition c = new CardDefinition(req.title(), req.rarity(), key);
        c.setTelegramUsername(normalizeCardUsername(req.telegramUsername()));
        return cardMapper.toResponse(cardDefinitionRepository.save(c));
    }

    @Transactional
    public CardDefinitionResponse update(long id, UpdateCardRequest req) {
        CardDefinition c =
                cardDefinitionRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new BusinessException(ErrorCode.NOT_FOUND, "Card not found"));
        c.setTitle(req.title());
        c.setRarity(req.rarity());
        c.setTelegramUsername(normalizeCardUsername(req.telegramUsername()));
        if (req.imageUrl() != null && !req.imageUrl().isBlank()) {
            c.setImageStorageKey(req.imageUrl().trim());
        }
        return cardMapper.toResponse(cardDefinitionRepository.save(c));
    }

    @Transactional
    public void delete(long id) {
        if (!cardDefinitionRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Card not found");
        }
        cardDefinitionRepository.deleteById(id);
    }

    private String normalizeCardUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        return UsernameNormalizer.normalize(username);
    }
}
