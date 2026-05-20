package com.casino.betting.exception;

import com.casino.common.exception.BusinessException;
import com.casino.common.exception.ErrorCode;

public class BettingException extends BusinessException {

    public BettingException(String message) {
        super(ErrorCode.CONFLICT, message);
    }
}
