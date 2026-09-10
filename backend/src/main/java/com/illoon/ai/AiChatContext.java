package com.illoon.ai;

import com.illoon.ai.domain.MeetingAnalysis;
import com.illoon.board.BoardService;
import com.illoon.board.dto.BoardResponse;
import com.illoon.meeting.MeetingRepository;
import com.illoon.meeting.domain.Meeting;
import com.illoon.project.domain.Project;
import com.illoon.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AI 어시스턴트 대화에 넣을 컨텍스트를 한 번의 짧은 트랜잭션으로 문자열화한다.
 * (느린 LLM 호출은 {@link AiChatService} 가 트랜잭션 밖에서 수행)
 */
@Component
@RequiredArgsConstructor
class AiChatContext {

    /** 회의 섹션 최대 길이 (문자). 이 안에서 최근 회의부터 채운다. */
    private static final int MEETING_BUDGET = 20_000;
    private static final int CONTENT_PREVIEW = 800;

    private final BoardService boardService;
    private final ProjectRepository projectRepository;
    private final MeetingRepository meetingRepository;
    private final MeetingAnalysisRepository analysisRepository;

    @Transactional(readOnly = true)
    public String render(Long userId) {
        StringBuilder sb = new StringBuilder();
        sb.append("[사용자 현황]\n").append(renderBoard(boardService.getBoard(userId)));
        sb.append("\n[회의 기록] (최근 순, 사용자가 참여 중인 프로젝트의 회의)\n")
                .append(renderMeetings(userId));
        return sb.toString();
    }

    // ---------- 현황 ----------

    private String renderBoard(BoardResponse b) {
        StringBuilder sb = new StringBuilder();
        sb.append("· 열린 내 업무 ").append(b.openTaskCount()).append("건")
                .append(" (3일 내 마감 ").append(b.dueSoonCount())
                .append(", 마감 지남 ").append(b.overdueCount())
                .append(", 오늘 마감 ").append(b.todayTaskCount()).append(")\n");
        sb.append("· 예정 회의 ").append(b.upcomingMeetingCount()).append("건\n");

        if (!b.projects().isEmpty()) {
            sb.append("· 프로젝트:\n");
            b.projects().forEach(p -> sb.append("   - ").append(p.name())
                    .append(" (").append(p.status())
                    .append(", 진행률 ").append(p.progress()).append("%")
                    .append(", 업무 ").append(p.taskCount()).append(")\n"));
        }
        if (!b.activeSprints().isEmpty()) {
            sb.append("· 활성 Sprint:\n");
            b.activeSprints().forEach(s -> sb.append("   - ").append(s.projectName())
                    .append(" / ").append(s.name())
                    .append(" (마감 ").append(s.endDate())
                    .append(", ").append(s.doneCount()).append("/").append(s.taskCount())
                    .append(" 완료)\n"));
        }
        if (!b.todayMeetings().isEmpty()) {
            sb.append("· 오늘 회의:\n");
            b.todayMeetings().forEach(m -> sb.append("   - ").append(m.title())
                    .append(m.meetingAt() != null ? " (" + m.meetingAt().toLocalTime() + ")" : "")
                    .append("\n"));
        }
        if (!b.todayTasks().isEmpty()) {
            sb.append("· 오늘 마감 업무:\n");
            b.todayTasks().forEach(t -> sb.append("   - ").append(t.title()).append("\n"));
        }
        return sb.toString();
    }

    // ---------- 회의 내용 ----------

    private String renderMeetings(Long userId) {
        List<Project> myProjects = projectRepository.findAllByMember(userId);
        if (myProjects.isEmpty()) return "(참여 중인 프로젝트 없음)\n";

        Map<Long, String> projectNames = myProjects.stream()
                .collect(Collectors.toMap(Project::getId, Project::getName, (a, x) -> a));
        List<Meeting> meetings = meetingRepository
                .findAllByProjectIdInOrderByMeetingAtDescCreatedAtDesc(projectNames.keySet());
        if (meetings.isEmpty()) return "(회의 없음)\n";

        StringBuilder sb = new StringBuilder();
        int shown = 0;
        for (Meeting m : meetings) {
            if (sb.length() > MEETING_BUDGET) {
                sb.append("\n… 이전 회의 ").append(meetings.size() - shown).append("건 생략\n");
                break;
            }
            sb.append("\n■ ").append(m.getTitle())
                    .append(" | ").append(projectNames.getOrDefault(m.getProjectId(), "?"));
            if (m.getMeetingAt() != null) sb.append(" | ").append(m.getMeetingAt().toLocalDate());
            sb.append("\n");

            MeetingAnalysis a = analysisRepository.findByMeetingId(m.getId()).orElse(null);
            if (a != null) {
                if (a.getOverview() != null && !a.getOverview().isBlank()) {
                    sb.append("  요약: ").append(a.getOverview()).append("\n");
                }
                if (!a.getDecisions().isEmpty()) {
                    sb.append("  결정: ").append(String.join(" / ", a.getDecisions())).append("\n");
                }
                if (!a.getActionPoints().isEmpty()) {
                    sb.append("  할일: ").append(a.getActionPoints().stream()
                            .map(ap -> ap.getTitle()
                                    + (ap.getAssigneeHint() != null ? "(" + ap.getAssigneeHint() + ")" : ""))
                            .collect(Collectors.joining(", "))).append("\n");
                }
            }
            String content = m.getContent();
            if (content != null && !content.isBlank()) {
                String preview = content.length() > CONTENT_PREVIEW
                        ? content.substring(0, CONTENT_PREVIEW) + "…"
                        : content;
                sb.append("  내용: ").append(preview.replaceAll("\\s+", " ").trim()).append("\n");
            }
            shown++;
        }
        return sb.toString();
    }
}
