package com.illoon.ai.dto;

import com.illoon.ai.domain.ActionPointItem;
import com.illoon.ai.domain.MeetingAnalysis;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record BriefingResponse(
        Long meetingId,
        String summary,
        List<String> decisions,
        List<ActionPointResponse> actionPoints,
        String source,
        LocalDateTime analyzedAt
) {
    public record ActionPointResponse(
            String title,
            String assigneeHint,
            LocalDate dueDate,
            String priority
    ) {
        static ActionPointResponse from(ActionPointItem i) {
            return new ActionPointResponse(
                    i.getTitle(), i.getAssigneeHint(), i.getDueDate(),
                    i.getPriority() == null ? "MEDIUM" : i.getPriority().name());
        }
    }

    public static BriefingResponse from(MeetingAnalysis a) {
        return new BriefingResponse(
                a.getMeetingId(),
                a.getSummary(),
                List.copyOf(a.getDecisions()),
                a.getActionPoints().stream().map(ActionPointResponse::from).toList(),
                a.getSource().name(),
                a.getCreatedAt());
    }
}
