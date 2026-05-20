package com.casino.quests.mapper;

import com.casino.quests.dto.response.QuestResponse;
import com.casino.quests.dto.response.QuestSubmissionResponse;
import com.casino.quests.entity.Quest;
import com.casino.quests.entity.QuestSubmission;
import org.springframework.stereotype.Component;

@Component
public class QuestMapper {

    public QuestResponse toResponse(Quest q) {
        return new QuestResponse(q.getId(), q.getTitle(), q.getRewardCoins(), q.getQuestStatus());
    }

    public QuestSubmissionResponse toSubmissionResponse(QuestSubmission s) {
        return new QuestSubmissionResponse(
                s.getId(), s.getQuestId(), s.getSubmissionStatus(), s.getProofText());
    }
}
