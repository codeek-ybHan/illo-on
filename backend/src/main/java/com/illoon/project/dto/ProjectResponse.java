package com.illoon.project.dto;

import com.illoon.project.domain.MemberRole;
import com.illoon.project.domain.Project;
import com.illoon.project.domain.ProjectStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProjectResponse(
        Long projectId,
        String name,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        ProjectStatus status,
        MemberRole myRole,
        long memberCount,
        LocalDateTime createdAt
) {
    public static ProjectResponse of(Project p, MemberRole myRole, long memberCount) {
        return new ProjectResponse(
                p.getId(), p.getName(), p.getDescription(),
                p.getStartDate(), p.getEndDate(), p.getStatus(),
                myRole, memberCount, p.getCreatedAt());
    }
}
