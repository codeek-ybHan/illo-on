package com.illoon.project.domain;

import com.illoon.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 기획서 §10-5 PROJECT_INVITE. 초대 링크 토큰.
 */
@Getter
@Entity
@Table(name = "project_invite")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectInvite extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invite_id")
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(nullable = false, unique = true, length = 64)
    private String token;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Builder
    private ProjectInvite(Long projectId, String token, Long createdBy, LocalDateTime expiresAt) {
        this.projectId = projectId;
        this.token = token;
        this.createdBy = createdBy;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }
}
