package com.illoon.common.dto;

import java.time.LocalDateTime;

/**
 * 공통 에러 응답. 프론트 axios 인터셉터가 message 를 읽는다.
 */
public record ErrorResponse(
        String code,
        String message,
        LocalDateTime timestamp
) {
    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, message, LocalDateTime.now());
    }
}
