package com.casino.admin.exception;

import com.casino.common.exception.BusinessException;
import com.casino.common.exception.ErrorCode;

public class AdminException extends BusinessException {

    public AdminException(String message) {
        super(ErrorCode.FORBIDDEN, message);
    }
}
