package com.casino.quests.exception;

import com.casino.common.exception.BusinessException;
import com.casino.common.exception.ErrorCode;

public class DuplicateQuestSubmissionException extends BusinessException {

    public DuplicateQuestSubmissionException() {
        super(ErrorCode.CONFLICT, "A pending submission already exists for this quest");
    }
}
