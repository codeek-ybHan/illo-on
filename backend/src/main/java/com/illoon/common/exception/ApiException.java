package com.illoon.common.exception;

import lombok.Getter;

/**
 * 서비스 계층에서 던지는 도메인 예외. GlobalExceptionHandler 가 ErrorCode 로 응답을 만든다.
 */
@Getter
public class ApiException extends RuntimeException {

    private final ErrorCode errorCode;

    public ApiException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ApiException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
