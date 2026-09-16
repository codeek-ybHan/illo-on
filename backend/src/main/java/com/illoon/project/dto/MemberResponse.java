package com.illoon.project.dto;

import com.illoon.project.domain.MemberRole;
import com.illoon.project.domain.ProjectMember;
import com.illoon.user.User;

public record MemberResponse(
        Long userId,
        String name,
        String email,
        MemberRole role,
        String jobTitle
) {
    public static MemberResponse of(User user, ProjectMember member) {
        return new MemberResponse(
                user.getId(), user.getName(), user.getEmail(), member.getRole(), member.getJobTitle());
    }
}
