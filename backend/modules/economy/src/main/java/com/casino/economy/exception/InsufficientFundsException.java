package com.casino.economy.exception;

import com.casino.common.exception.BusinessException;
import com.casino.common.exception.ErrorCode;

public class InsufficientFundsException extends BusinessException {

    public InsufficientFundsException() {
        super(ErrorCode.ECONOMY_INSUFFICIENT_FUNDS, "Insufficient coins");
    }
}
