package com.illoon.auth.dto;

import com.illoon.user.User;
import com.illoon.user.dto.UserResponse;

/**
 * 로그인 응답 — JWT + 사용자 정보.
 */
public record AuthResponse(
        String token,
        UserResponse user
) {
    public static AuthResponse of(String token, User user) {
        return new AuthResponse(token, UserResponse.from(user));
    }
}
