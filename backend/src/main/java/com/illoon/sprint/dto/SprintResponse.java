package com.illoon.sprint.dto;

import com.illoon.sprint.domain.Sprint;
import com.illoon.sprint.domain.SprintStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SprintResponse(
        Long sprintId,
        Long projectId,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        SprintStatus status,
        long taskCount,
        long doneCount,
        int progress,
        LocalDateTime createdAt
) {
    public static SprintResponse of(Sprint s, long taskCount, long doneCount) {
        int progress = taskCount == 0 ? 0 : (int) Math.round(doneCount * 100.0 / taskCount);
        return new SprintResponse(
                s.getId(), s.getProjectId(), s.getName(),
                s.getStartDate(), s.getEndDate(), s.getStatus(),
                taskCount, doneCount, progress, s.getCreatedAt());
    }
}
