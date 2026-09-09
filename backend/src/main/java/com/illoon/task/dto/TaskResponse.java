package com.illoon.task.dto;

import com.illoon.task.domain.Task;
import com.illoon.task.domain.TaskPriority;
import com.illoon.task.domain.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskResponse(
        Long taskId,
        Long projectId,
        String projectName,
        Long meetingId,
        Long sprintId,
        String title,
        String description,
        Long assigneeId,
        String assigneeName,
        LocalDate dueDate,
        TaskPriority priority,
        TaskStatus status,
        LocalDateTime createdAt
) {
    public static TaskResponse of(Task t, String projectName, String assigneeName) {
        return new TaskResponse(
                t.getId(), t.getProjectId(), projectName,
                t.getMeetingId(), t.getSprintId(),
                t.getTitle(), t.getDescription(),
                t.getAssigneeId(), assigneeName,
                t.getDueDate(), t.getPriority(), t.getStatus(),
                t.getCreatedAt());
    }
}
