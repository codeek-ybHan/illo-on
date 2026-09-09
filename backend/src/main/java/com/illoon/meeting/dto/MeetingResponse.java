package com.illoon.meeting.dto;

import com.illoon.meeting.domain.Meeting;

import java.time.LocalDateTime;

/**
 * 회의 목록용 요약. hasSummary / taskCount 는 AI(Phase 6) 이후 의미를 가진다.
 */
public record MeetingResponse(
        Long meetingId,
        Long projectId,
        String projectName,
        String title,
        LocalDateTime meetingAt,
        long attendeeCount,
        boolean hasContent,
        boolean hasSummary,
        long taskCount,
        LocalDateTime createdAt
) {
    public static MeetingResponse of(Meeting m, String projectName,
                                     long attendeeCount, boolean hasSummary, long taskCount) {
        return new MeetingResponse(
                m.getId(), m.getProjectId(), projectName, m.getTitle(), m.getMeetingAt(),
                attendeeCount,
                m.getContent() != null && !m.getContent().isBlank(),
                hasSummary, taskCount, m.getCreatedAt());
    }
}
