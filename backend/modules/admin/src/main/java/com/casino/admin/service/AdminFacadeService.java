package com.casino.admin.service;

import com.casino.cards.repository.CardDefinitionRepository;
import com.casino.quests.bot.repo.QuestTaskRepository;
import com.casino.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminFacadeService {

    private final UserRepository userRepository;
    private final CardDefinitionRepository cardDefinitionRepository;
    private final QuestTaskRepository questTaskRepository;

    @Transactional(readOnly = true)
    public AdminStatsResponse stats() {
        long users = userRepository.count();
        long cards = cardDefinitionRepository.count();
        long quests = questTaskRepository.count();
        return new AdminStatsResponse(users, cards, quests);
    }

    public record AdminStatsResponse(long users, long cardDefinitions, long quests) {}
}
