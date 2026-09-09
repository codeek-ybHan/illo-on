package com.illoon.meeting.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record MeetingCreateRequest(
        @NotBlank(message = "회의 제목을 입력해 주세요.")
        @Size(max = 200, message = "회의 제목은 200자 이하여야 합니다.")
        String title,

        String content,
        LocalDateTime meetingAt,

        /** 참석자 userId 목록 (프로젝트 멤버만) */
        List<Long> attendeeIds
) {}
