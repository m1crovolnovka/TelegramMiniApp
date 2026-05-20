package com.casino.auth.exception;

import com.casino.common.exception.BusinessException;
import com.casino.common.exception.ErrorCode;

public class AuthException extends BusinessException {

    public AuthException(String message) {
        super(ErrorCode.UNAUTHORIZED, message);
    }
}
