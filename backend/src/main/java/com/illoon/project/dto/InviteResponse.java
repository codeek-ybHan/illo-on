package com.illoon.project.dto;

import com.illoon.project.domain.ProjectInvite;

import java.time.LocalDateTime;

public record InviteResponse(
        String token,
        String inviteUrl,
        LocalDateTime expiresAt
) {
    public static InviteResponse of(ProjectInvite invite, String inviteUrl) {
        return new InviteResponse(invite.getToken(), inviteUrl, invite.getExpiresAt());
    }
}
