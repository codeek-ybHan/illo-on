package com.illoon.ai.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI 브리핑 수동 수정. 재분석이 아니라 저장된 결과를 그대로 덮어쓴다.
 */
public record BriefingUpdateRequest(
        @Size(max = 2000, message = "한눈에 보기 요약은 2000자 이하여야 합니다.")
        String overview,

        List<@Size(max = 500, message = "항목은 500자 이하여야 합니다.") String> highlights,

        List<@Size(max = 500, message = "항목은 500자 이하여야 합니다.") String> decisions,

        List<ActionPointRequest> actionPoints
) {
    public record ActionPointRequest(
            @Size(max = 300, message = "업무명은 300자 이하여야 합니다.")
            String title,

            @Size(max = 50, message = "담당자 이름은 50자 이하여야 합니다.")
            String assigneeHint,

            LocalDateTime dueDate,

            /** HIGH | MEDIUM | LOW. 그 외 값은 MEDIUM 으로 처리. */
            String priority
    ) {}
}
