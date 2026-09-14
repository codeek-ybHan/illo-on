package com.illoon.meeting.dto;

import com.illoon.meeting.domain.Meeting;

import java.time.LocalDateTime;
import java.util.List;

public record MeetingDetailResponse(
        Long meetingId,
        Long projectId,
        String projectName,
        String title,
        String content,
        LocalDateTime meetingAt,
        List<Attendee> attendees,
        /** 프로젝트 멤버가 아닌 외부 참석자 이름 */
        List<String> guestNames,
        boolean hasSummary,
        long taskCount,
        LocalDateTime createdAt
) {
    public record Attendee(Long userId, String name, String email) {}

    public static MeetingDetailResponse of(Meeting m, String projectName, List<Attendee> attendees,
                                           List<String> guestNames, boolean hasSummary, long taskCount) {
        return new MeetingDetailResponse(
                m.getId(), m.getProjectId(), projectName, m.getTitle(), m.getContent(),
                m.getMeetingAt(), attendees, guestNames, hasSummary, taskCount, m.getCreatedAt());
    }
}
