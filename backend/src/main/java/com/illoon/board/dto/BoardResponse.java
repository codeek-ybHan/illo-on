package com.illoon.board.dto;

import com.illoon.meeting.dto.MeetingResponse;
import com.illoon.task.dto.TaskResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * 메인보드 한 화면에 필요한 집계.
 */
public record BoardResponse(
        int openTaskCount,       // 내 미완료 Task
        int dueSoonCount,        // 3일 내 마감
        int overdueCount,        // 마감 지남 + 미완료
        int todayTaskCount,
        int upcomingMeetingCount,
        List<BoardProject> projects,
        List<BoardSprint> activeSprints,
        List<MeetingResponse> todayMeetings,
        List<TaskResponse> todayTasks
) {
    public record BoardProject(
            Long projectId, String name, String status,
            int progress, long taskCount, long memberCount) {}

    public record BoardSprint(
            Long sprintId, Long projectId, String projectName, String name,
            LocalDate endDate, int progress, long taskCount, long doneCount) {}
}
