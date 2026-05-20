package com.casino.quests.service;

import com.casino.quests.dto.response.QuestResponse;
import com.casino.quests.entity.QuestStatus;
import com.casino.quests.mapper.QuestMapper;
import com.casino.quests.repository.QuestRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestService {

    private final QuestRepository questRepository;
    private final QuestMapper questMapper;

    @Transactional(readOnly = true)
    public List<QuestResponse> listActive() {
        return questRepository.findByQuestStatus(QuestStatus.ACTIVE).stream()
                .map(questMapper::toResponse)
                .toList();
    }
}
