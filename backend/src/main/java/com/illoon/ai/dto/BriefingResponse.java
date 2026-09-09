package com.illoon.ai.dto;

import com.illoon.ai.domain.ActionPointItem;
import com.illoon.ai.domain.MeetingAnalysis;

import java.time.LocalDateTime;
import java.util.List;

public record BriefingResponse(
        Long meetingId,
        String overview,
        List<String> highlights,
        List<String> decisions,
        List<ActionPointResponse> actionPoints,
        String source,
        /** "mock" | "openai" — 요약 품질 안내용 */
        String provider,
        LocalDateTime analyzedAt
) {
    public record ActionPointResponse(
            String title,
            String assigneeHint,
            LocalDateTime dueDate,
            String priority
    ) {
        static ActionPointResponse from(ActionPointItem i) {
            return new ActionPointResponse(
                    i.getTitle(), i.getAssigneeHint(), i.getDueDate(),
                    i.getPriority() == null ? "MEDIUM" : i.getPriority().name());
        }
    }

    public static BriefingResponse from(MeetingAnalysis a, String provider) {
        return new BriefingResponse(
                a.getMeetingId(),
                a.getOverview(),
                List.copyOf(a.getHighlights()),
                List.copyOf(a.getDecisions()),
                a.getActionPoints().stream().map(ActionPointResponse::from).toList(),
                a.getSource().name(),
                provider,
                a.getCreatedAt());
    }
}
