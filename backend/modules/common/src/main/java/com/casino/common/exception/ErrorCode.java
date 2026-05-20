package com.casino.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    VALIDATION_ERROR("VALIDATION_ERROR"),
    NOT_FOUND("NOT_FOUND"),
    CONFLICT("CONFLICT"),
    FORBIDDEN("FORBIDDEN"),
    UNAUTHORIZED("UNAUTHORIZED"),
    ECONOMY_INSUFFICIENT_FUNDS("ECONOMY_INSUFFICIENT_FUNDS"),
    ECONOMY_DUPLICATE_OPERATION("ECONOMY_DUPLICATE_OPERATION"),
    BAD_REQUEST("BAD_REQUEST");

    private final String code;
}
