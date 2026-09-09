package com.illoon.task.dto;

import com.illoon.task.domain.TaskPriority;
import com.illoon.task.domain.TaskStatus;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * PUT — 전체 교체. 상태 변경도 이 엔드포인트로 (별도 status API 없음).
 */
public record TaskUpdateRequest(
        @Size(max = 200, message = "업무명은 200자 이하여야 합니다.")
        String title,

        String description,
        Long assigneeId,
        LocalDateTime dueDate,
        TaskPriority priority,
        TaskStatus status,
        Long sprintId,
        Long meetingId
) {}
