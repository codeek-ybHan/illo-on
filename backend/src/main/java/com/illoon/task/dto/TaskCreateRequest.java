package com.illoon.task.dto;

import com.illoon.task.domain.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record TaskCreateRequest(
        @NotNull(message = "프로젝트를 지정해 주세요.")
        Long projectId,

        @NotBlank(message = "업무명을 입력해 주세요.")
        @Size(max = 200, message = "업무명은 200자 이하여야 합니다.")
        String title,

        String description,
        Long assigneeId,
        LocalDateTime dueDate,
        TaskPriority priority,

        /** 회의에서 등록된 경우 생성 맥락 회의 (기획서 §5-7) */
        Long meetingId,

        /** 생성과 동시에 Sprint 배정 (선택) */
        Long sprintId
) {}
