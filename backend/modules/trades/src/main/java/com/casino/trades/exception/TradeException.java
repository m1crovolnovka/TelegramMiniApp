package com.casino.trades.exception;

import com.casino.common.exception.BusinessException;
import com.casino.common.exception.ErrorCode;

public class TradeException extends BusinessException {

    public TradeException(String message) {
        super(ErrorCode.CONFLICT, message);
    }
}
