package com.casino.quests.service;

import com.casino.quests.dto.request.CreateQuestRequest;
import com.casino.quests.dto.response.QuestResponse;
import com.casino.quests.entity.Quest;
import com.casino.quests.entity.QuestStatus;
import com.casino.quests.mapper.QuestMapper;
import com.casino.quests.repository.QuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestManagementService {

    private final QuestRepository questRepository;
    private final QuestMapper questMapper;

    @Transactional
    public QuestResponse createQuest(CreateQuestRequest request) {
        Quest q = questRepository.save(new Quest(request.title(), request.rewardCoins(), QuestStatus.ACTIVE));
        return questMapper.toResponse(q);
    }
}
