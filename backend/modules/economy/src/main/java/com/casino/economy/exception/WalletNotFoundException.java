package com.casino.economy.exception;

import com.casino.common.exception.BusinessException;
import com.casino.common.exception.ErrorCode;

public class WalletNotFoundException extends BusinessException {

    public WalletNotFoundException() {
        super(ErrorCode.NOT_FOUND, "Wallet not found");
    }
}
