package com.casino.packs.exception;

import com.casino.common.exception.BusinessException;
import com.casino.common.exception.ErrorCode;

public class PackNotFoundException extends BusinessException {

    public PackNotFoundException() {
        super(ErrorCode.NOT_FOUND, "Pack not found");
    }
}
