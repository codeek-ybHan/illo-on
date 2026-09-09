package com.illoon.project.dto;

import com.illoon.project.domain.MemberRole;
import com.illoon.user.User;

public record MemberResponse(
        Long userId,
        String name,
        String email,
        MemberRole role
) {
    public static MemberResponse of(User user, MemberRole role) {
        return new MemberResponse(user.getId(), user.getName(), user.getEmail(), role);
    }
}
