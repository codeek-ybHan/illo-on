package com.illoon.sprint.dto;

import com.illoon.sprint.domain.SprintStatus;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record SprintUpdateRequest(
        @Size(max = 100, message = "Sprint 이름은 100자 이하여야 합니다.")
        String name,

        LocalDate startDate,
        LocalDate endDate,
        SprintStatus status
) {}
