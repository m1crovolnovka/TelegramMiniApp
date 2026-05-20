package com.casino.quests.service;

import com.casino.common.exception.BusinessException;
import com.casino.common.exception.ErrorCode;
import com.casino.quests.entity.Quest;
import com.casino.quests.entity.QuestStatus;
import com.casino.quests.entity.QuestSubmission;
import com.casino.quests.entity.QuestSubmission.QuestSubmissionStatus;
import com.casino.quests.entity.UserQuest;
import com.casino.quests.exception.QuestNotFoundException;
import com.casino.quests.repository.QuestRepository;
import com.casino.quests.repository.QuestSubmissionRepository;
import com.casino.quests.repository.UserQuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestModerationService {

    private final QuestSubmissionRepository questSubmissionRepository;
    private final QuestRepository questRepository;
    private final QuestRewardService questRewardService;
    private final UserQuestRepository userQuestRepository;

    @Transactional
    public void approve(long submissionId) {
        QuestSubmission s =
                questSubmissionRepository
                        .findByIdAndSubmissionStatus(submissionId, QuestSubmissionStatus.PENDING)
                        .orElseThrow(() -> new BusinessException(ErrorCode.CONFLICT, "Submission not pending"));
        Quest quest = questRepository.findById(s.getQuestId()).orElseThrow(QuestNotFoundException::new);
        if (quest.getQuestStatus() != QuestStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.CONFLICT, "Quest is not active");
        }
        s.setSubmissionStatus(QuestSubmissionStatus.APPROVED);
        questRewardService.payReward(s.getUserId(), quest.getRewardCoins(), submissionId);
        UserQuest uq =
                userQuestRepository
                        .findByUserIdAndQuestId(s.getUserId(), s.getQuestId())
                        .orElseGet(
                                () -> userQuestRepository.save(new UserQuest(s.getUserId(), s.getQuestId(), false)));
        uq.setCompleted(true);
        userQuestRepository.save(uq);
    }

    @Transactional
    public void reject(long submissionId) {
        QuestSubmission s =
                questSubmissionRepository
                        .findByIdAndSubmissionStatus(submissionId, QuestSubmissionStatus.PENDING)
                        .orElseThrow(() -> new BusinessException(ErrorCode.CONFLICT, "Submission not pending"));
        s.setSubmissionStatus(QuestSubmissionStatus.REJECTED);
    }
}
