package com.illoon.admin.dto;

import com.illoon.user.User;

import java.time.LocalDateTime;

public record AdminUserResponse(
        Long userId,
        String name,
        String email,
        boolean isAdmin,
        LocalDateTime createdAt
) {
    public static AdminUserResponse from(User user) {
        return new AdminUserResponse(
                user.getId(), user.getName(), user.getEmail(), user.isAdmin(), user.getCreatedAt());
    }
}
