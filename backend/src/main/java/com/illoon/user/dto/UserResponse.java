package com.illoon.user.dto;

import com.illoon.user.User;

/**
 * 사용자 공개 정보 (비밀번호 제외).
 */
public record UserResponse(
        Long userId,
        String name,
        String email
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }
}
