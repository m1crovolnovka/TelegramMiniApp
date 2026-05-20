package com.casino.cards.exception;

import com.casino.common.exception.BusinessException;
import com.casino.common.exception.ErrorCode;

public class CardNotFoundException extends BusinessException {

    public CardNotFoundException() {
        super(ErrorCode.NOT_FOUND, "Card not found");
    }
}
