package com.casino.casino.exception;

import com.casino.common.exception.BusinessException;
import com.casino.common.exception.ErrorCode;

public class CasinoException extends BusinessException {

    public CasinoException(String message) {
        super(ErrorCode.CONFLICT, message);
    }
}
