package com.illoon.sprint.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record SprintCreateRequest(
        @NotBlank(message = "Sprint 이름을 입력해 주세요.")
        @Size(max = 100, message = "Sprint 이름은 100자 이하여야 합니다.")
        String name,

        LocalDate startDate,
        LocalDate endDate
) {}
