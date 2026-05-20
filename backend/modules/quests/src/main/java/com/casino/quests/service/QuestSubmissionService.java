package com.casino.quests.service;

import com.casino.common.exception.BusinessException;
import com.casino.common.exception.ErrorCode;
import com.casino.quests.dto.request.SubmitQuestRequest;
import com.casino.quests.dto.response.QuestSubmissionResponse;
import com.casino.quests.entity.Quest;
import com.casino.quests.entity.QuestStatus;
import com.casino.quests.entity.QuestSubmission;
import com.casino.quests.entity.QuestSubmission.QuestSubmissionStatus;
import com.casino.quests.exception.DuplicateQuestSubmissionException;
import com.casino.quests.exception.QuestNotFoundException;
import com.casino.quests.mapper.QuestMapper;
import com.casino.quests.repository.QuestRepository;
import com.casino.quests.repository.QuestSubmissionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestSubmissionService {

    private final QuestRepository questRepository;
    private final QuestSubmissionRepository questSubmissionRepository;
    private final QuestMapper questMapper;

    @Transactional
    public QuestSubmissionResponse submit(long userId, SubmitQuestRequest request) {
        Quest quest = questRepository.findById(request.questId()).orElseThrow(QuestNotFoundException::new);
        if (quest.getQuestStatus() != QuestStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.CONFLICT, "Quest is not active");
        }
        if (questSubmissionRepository.existsByQuestIdAndUserIdAndSubmissionStatus(
                request.questId(), userId, QuestSubmissionStatus.PENDING)) {
            throw new DuplicateQuestSubmissionException();
        }
        QuestSubmission s =
                questSubmissionRepository.save(
                        new QuestSubmission(request.questId(), userId, request.proofText()));
        return questMapper.toSubmissionResponse(s);
    }

    @Transactional(readOnly = true)
    public List<QuestSubmissionResponse> mySubmissions(long userId) {
        return questSubmissionRepository.findByUserIdOrderByIdDesc(userId).stream()
                .map(questMapper::toSubmissionResponse)
                .toList();
    }
}
