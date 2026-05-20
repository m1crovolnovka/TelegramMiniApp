package com.casino.cards.exception;

import com.casino.common.exception.BusinessException;
import com.casino.common.exception.ErrorCode;

public class InsufficientCardQuantityException extends BusinessException {

    public InsufficientCardQuantityException() {
        super(ErrorCode.CONFLICT, "Not enough cards in inventory");
    }
}
