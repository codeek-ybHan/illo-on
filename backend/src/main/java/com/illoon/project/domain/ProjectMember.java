package com.illoon.project.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 기획서 §10-4 PROJECT_MEMBER. 복합 PK (project_id, user_id).
 */
@Getter
@Entity
@Table(name = "project_member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectMember {

    @EmbeddedId
    private Pk id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private MemberRole role;

    @Builder
    private ProjectMember(Long projectId, Long userId, MemberRole role) {
        this.id = new Pk(projectId, userId);
        this.role = role;
    }

    public Long getProjectId() {
        return id.projectId;
    }

    public Long getUserId() {
        return id.userId;
    }

    public boolean isAdmin() {
        return role == MemberRole.ADMIN;
    }

    @Getter
    @Embeddable
    @EqualsAndHashCode
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Pk implements Serializable {
        @Column(name = "project_id")
        private Long projectId;

        @Column(name = "user_id")
        private Long userId;

        public Pk(Long projectId, Long userId) {
            this.projectId = projectId;
            this.userId = userId;
        }
    }
}
