package com.casino.cards.service;

import com.casino.cards.dto.response.CollectionProgressResponse;
import com.casino.cards.dto.response.CollectionProgressResponse.CardRarityBreakdown;
import com.casino.cards.entity.CardDefinition;
import com.casino.cards.entity.CardRarity;
import com.casino.cards.entity.UserCard;
import com.casino.cards.repository.CardDefinitionRepository;
import com.casino.cards.repository.UserCardRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final CardDefinitionRepository cardDefinitionRepository;
    private final UserCardRepository userCardRepository;

    @Transactional(readOnly = true)
    public CollectionProgressResponse progress(long userId) {
        List<CardDefinition> allDefs = cardDefinitionRepository.findAll();
        long totalDefs = allDefs.size();
        List<UserCard> inv = userCardRepository.findByUserId(userId);
        Set<Long> ownedIds =
                inv.stream()
                        .filter(uc -> uc.getQuantity() > 0)
                        .map(UserCard::getCardDefinitionId)
                        .collect(Collectors.toSet());
        long ownedDefs = ownedIds.size();
        double pct = totalDefs == 0 ? 0 : (ownedDefs * 100.0 / totalDefs);

        var byId = allDefs.stream().collect(Collectors.toMap(CardDefinition::getId, c -> c));
        long c = 0, r = 0, l = 0;
        Set<String> ownedStudents = new HashSet<>();
        for (Long id : ownedIds) {
            CardDefinition def = byId.get(id);
            if (def == null) {
                continue;
            }
            switch (def.getRarity()) {
                case COMMON -> c++;
                case RARE -> r++;
                case LEGENDARY -> l++;
            }
            if (def.getTelegramUsername() != null && !def.getTelegramUsername().isBlank()) {
                ownedStudents.add(def.getTelegramUsername().trim().toLowerCase());
            }
        }

        Set<String> allStudents =
                allDefs.stream()
                        .map(CardDefinition::getTelegramUsername)
                        .filter(u -> u != null && !u.isBlank())
                        .map(u -> u.trim().toLowerCase())
                        .collect(Collectors.toSet());
        long totalStudents = allStudents.size();
        long ownedStudentCount = ownedStudents.size();
        double studentPct = totalStudents == 0 ? 0 : (ownedStudentCount * 100.0 / totalStudents);

        return new CollectionProgressResponse(
                ownedDefs,
                totalDefs,
                pct,
                ownedStudentCount,
                totalStudents,
                studentPct,
                new CardRarityBreakdown(c, r, l));
    }

    @Transactional(readOnly = true)
    public long countUniqueStudentsOwned(long userId) {
        return progress(userId).ownedUniqueStudents();
    }
}
