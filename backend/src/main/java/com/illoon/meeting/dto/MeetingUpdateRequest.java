package com.illoon.meeting.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record MeetingUpdateRequest(
        @Size(max = 200, message = "회의 제목은 200자 이하여야 합니다.")
        String title,

        String content,
        LocalDateTime meetingAt,
        List<Long> attendeeIds,

        /** 프로젝트 멤버가 아닌 외부 참석자 이름 목록 */
        List<@Size(max = 50, message = "외부 참석자 이름은 50자 이하여야 합니다.") String> guestNames
) {}
