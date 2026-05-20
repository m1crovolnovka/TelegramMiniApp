package com.casino.notifications.exception;

import com.casino.common.exception.BusinessException;
import com.casino.common.exception.ErrorCode;

public class NotificationException extends BusinessException {

    public NotificationException(String message) {
        super(ErrorCode.NOT_FOUND, message);
    }
}
