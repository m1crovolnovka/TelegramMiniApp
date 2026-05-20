package com.casino.users.exception;

import com.casino.common.exception.BusinessException;
import com.casino.common.exception.ErrorCode;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException() {
        super(ErrorCode.NOT_FOUND, "User not found");
    }
}
