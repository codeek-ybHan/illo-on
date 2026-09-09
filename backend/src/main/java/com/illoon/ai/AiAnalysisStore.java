package com.illoon.ai;

import com.illoon.ai.domain.ActionPointItem;
import com.illoon.ai.domain.AnalysisSource;
import com.illoon.ai.domain.MeetingAnalysis;
import com.illoon.ai.dto.BriefingResponse;
import com.illoon.common.exception.ApiException;
import com.illoon.common.exception.ErrorCode;
import com.illoon.meeting.MeetingRepository;
import com.illoon.meeting.domain.Meeting;
import com.illoon.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * AI 분석의 DB 접근만 담당 (짧은 트랜잭션).
 * 느린 OpenAI 호출(STT·LLM)은 {@link AiService} 가 트랜잭션 밖에서 수행한다 —
 * 수십 초짜리 외부 호출이 DB 커넥션·트랜잭션을 붙잡지 않도록.
 */
@Component
@RequiredArgsConstructor
class AiAnalysisStore {

    private final MeetingRepository meetingRepository;
    private final MeetingAnalysisRepository analysisRepository;
    private final ProjectService projectService;

    /** 회의 조회 + 멤버 권한 확인. */
    @Transactional(readOnly = true)
    public Meeting loadForMember(Long meetingId, Long userId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new ApiException(ErrorCode.MEETING_NOT_FOUND));
        projectService.requireMember(meeting.getProjectId(), userId);
        return meeting;
    }

    /**
     * 분석 결과 저장. AUDIO 면 STT 텍스트를 회의 내용에 반영한다.
     * 컬렉션 접근이 트랜잭션 안에서 끝나도록 여기서 BriefingResponse 까지 만들어 반환.
     */
    @Transactional
    public BriefingResponse save(Long meetingId, AnalysisSource source, String sttText,
                                 String overview, List<String> highlights, List<String> decisions,
                                 List<ActionPointItem> actionPoints, String provider) {
        if (source == AnalysisSource.AUDIO && sttText != null && !sttText.isBlank()) {
            Meeting meeting = meetingRepository.findById(meetingId)
                    .orElseThrow(() -> new ApiException(ErrorCode.MEETING_NOT_FOUND));
            meeting.update(meeting.getTitle(), sttText, meeting.getMeetingAt());
        }

        MeetingAnalysis analysis = analysisRepository.findByMeetingId(meetingId)
                .orElseGet(() -> new MeetingAnalysis(meetingId, source));
        analysis.apply(overview, source, highlights, decisions, actionPoints);
        analysisRepository.save(analysis);

        return BriefingResponse.from(analysis, provider);
    }
}
