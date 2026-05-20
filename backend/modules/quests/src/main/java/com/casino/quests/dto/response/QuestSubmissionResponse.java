package com.casino.quests.dto.response;

import com.casino.quests.entity.QuestSubmission.QuestSubmissionStatus;

public record QuestSubmissionResponse(
        long id, long questId, QuestSubmissionStatus status, String proofText) {}
