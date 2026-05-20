package com.casino.cards.exception;

import com.casino.common.exception.BusinessException;
import com.casino.common.exception.ErrorCode;

public class CardLockedException extends BusinessException {

    public CardLockedException() {
        super(ErrorCode.CONFLICT, "Card is locked for an active trade");
    }
}
