package com.casino.cards.service;

import com.casino.cards.api.InventoryPort;
import com.casino.cards.entity.UserCard;
import com.casino.cards.exception.CardLockedException;
import com.casino.cards.exception.InsufficientCardQuantityException;
import com.casino.cards.repository.UserCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService implements InventoryPort {

    private final UserCardRepository userCardRepository;

    @Override
    @Transactional
    public void addCard(long userId, long cardDefinitionId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity");
        }
        var locked = userCardRepository.lockByUserAndCard(userId, cardDefinitionId);
        if (locked.isPresent()) {
            UserCard row = locked.get();
            row.setQuantity(row.getQuantity() + quantity);
            return;
        }
        try {
            userCardRepository.save(new UserCard(userId, cardDefinitionId, quantity));
        } catch (DataIntegrityViolationException e) {
            UserCard row =
                    userCardRepository
                            .lockByUserAndCard(userId, cardDefinitionId)
                            .orElseThrow(() -> e);
            row.setQuantity(row.getQuantity() + quantity);
        }
    }

    @Override
    @Transactional
    public void removeCard(long userId, long cardDefinitionId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity");
        }
        UserCard row =
                userCardRepository
                        .lockByUserAndCard(userId, cardDefinitionId)
                        .orElseThrow(InsufficientCardQuantityException::new);
        if (row.isLocked()) {
            throw new CardLockedException();
        }
        if (row.getQuantity() < quantity) {
            throw new InsufficientCardQuantityException();
        }
        row.setQuantity(row.getQuantity() - quantity);
        if (row.getQuantity() == 0) {
            userCardRepository.delete(row);
        }
    }

    @Override
    @Transactional
    public void setLockedForTrade(long userId, long cardDefinitionId, boolean locked, Long tradeId) {
        UserCard row =
                userCardRepository
                        .lockByUserAndCard(userId, cardDefinitionId)
                        .orElseThrow(InsufficientCardQuantityException::new);
        row.setLocked(locked);
        row.setLockedTradeId(locked ? tradeId : null);
    }
}
