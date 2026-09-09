package com.illoon.ai;

import com.illoon.ai.analyzer.AiAnalyzer;
import com.illoon.ai.analyzer.Briefing;
import com.illoon.ai.domain.ActionPointItem;
import com.illoon.ai.domain.AnalysisSource;
import com.illoon.ai.domain.MeetingAnalysis;
import com.illoon.ai.dto.BriefingResponse;
import com.illoon.ai.stt.SpeechToText;
import com.illoon.common.exception.ApiException;
import com.illoon.common.exception.ErrorCode;
import com.illoon.meeting.MeetingRepository;
import com.illoon.meeting.domain.Meeting;
import com.illoon.project.ProjectService;
import com.illoon.task.domain.TaskPriority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final MeetingRepository meetingRepository;
    private final MeetingAnalysisRepository analysisRepository;
    private final ProjectService projectService;
    private final AiAnalyzer analyzer;
    private final SpeechToText speechToText;

    /**
     * 회의 분석. audio 가 있으면 STT 로 텍스트를 만들고 회의 내용에 저장한 뒤 분석한다.
     * 없으면 회의에 저장된 내용을 분석한다. (기획서 §11-6)
     */
    @Transactional
    public BriefingResponse analyze(Long meetingId, Long userId, MultipartFile audio) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new ApiException(ErrorCode.MEETING_NOT_FOUND));
        projectService.requireMember(meeting.getProjectId(), userId);

        AnalysisSource source;
        String text;
        if (audio != null && !audio.isEmpty()) {
            text = speechToText.transcribe(audio);
            meeting.update(meeting.getTitle(), text, meeting.getMeetingAt()); // STT 결과 저장
            source = AnalysisSource.AUDIO;
        } else {
            text = meeting.getContent();
            source = AnalysisSource.TEXT;
        }
        if (text == null || text.isBlank()) {
            throw new ApiException(ErrorCode.MEETING_CONTENT_EMPTY);
        }

        Briefing briefing = analyzer.analyze(text);

        MeetingAnalysis analysis = analysisRepository.findByMeetingId(meetingId)
                .orElseGet(() -> new MeetingAnalysis(meetingId, source));
        analysis.apply(
                nullToEmpty(briefing.summary()),
                source,
                safe(briefing.decisions()),
                toItems(safe(briefing.actionPoints())));
        analysisRepository.save(analysis);

        return BriefingResponse.from(analysis);
    }

    @Transactional(readOnly = true)
    public BriefingResponse getSummary(Long meetingId, Long userId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new ApiException(ErrorCode.MEETING_NOT_FOUND));
        projectService.requireMember(meeting.getProjectId(), userId);
        return analysisRepository.findByMeetingId(meetingId)
                .map(BriefingResponse::from)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "아직 AI 분석 결과가 없습니다."));
    }

    // ---------- mapping ----------

    private List<ActionPointItem> toItems(List<Briefing.ActionPoint> aps) {
        return aps.stream()
                .filter(ap -> ap.title() != null && !ap.title().isBlank())
                .map(ap -> ActionPointItem.builder()
                        .title(ap.title().trim())
                        .assigneeHint(blankToNull(ap.assignee()))
                        .dueDate(parseDate(ap.dueDate()))
                        .priority(parsePriority(ap.priority()))
                        .build())
                .toList();
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return LocalDate.parse(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private TaskPriority parsePriority(String s) {
        if (s == null) return TaskPriority.MEDIUM;
        try {
            return TaskPriority.valueOf(s.trim().toUpperCase());
        } catch (Exception e) {
            return TaskPriority.MEDIUM;
        }
    }

    private <T> List<T> safe(List<T> list) {
        return list == null ? List.of() : list;
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    // MeetingService 에서 hasSummary 판단용
    public boolean hasAnalysis(Long meetingId) {
        return analysisRepository.existsByMeetingId(meetingId);
    }

    public Optional<BriefingResponse> findAnalysis(Long meetingId) {
        return analysisRepository.findByMeetingId(meetingId).map(BriefingResponse::from);
    }
}
