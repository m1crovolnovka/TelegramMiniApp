package com.casino.quests.exception;

import com.casino.common.exception.BusinessException;
import com.casino.common.exception.ErrorCode;

public class QuestNotFoundException extends BusinessException {

    public QuestNotFoundException() {
        super(ErrorCode.NOT_FOUND, "Quest not found");
    }
}
