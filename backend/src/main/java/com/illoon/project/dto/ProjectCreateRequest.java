package com.illoon.project.dto;

import com.illoon.project.domain.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ProjectCreateRequest(
        @NotBlank(message = "프로젝트명을 입력해 주세요.")
        @Size(max = 100, message = "프로젝트명은 100자 이하여야 합니다.")
        String name,

        String description,
        LocalDate startDate,
        LocalDate endDate,

        /** 미지정 시 PLANNED */
        ProjectStatus status
) {}
