package com.illoon.ai.domain;

import com.illoon.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 회의 AI 분석 결과. 회의당 1건, 재분석 시 덮어쓴다.
 * (기획서 §10-10 — ACTION_POINT 별도 테이블 대신 결과를 여기에 보관)
 */
@Getter
@Entity
@Table(name = "meeting_analysis")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeetingAnalysis extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_id")
    private Long id;

    @Column(name = "meeting_id", nullable = false, unique = true)
    private Long meetingId;

    @Column(columnDefinition = "text")
    private String summary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AnalysisSource source;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "meeting_analysis_decision", joinColumns = @JoinColumn(name = "analysis_id"))
    @Column(name = "decision", length = 500)
    @OrderColumn(name = "seq")
    private List<String> decisions = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "meeting_analysis_action_point", joinColumns = @JoinColumn(name = "analysis_id"))
    @OrderColumn(name = "seq")
    private List<ActionPointItem> actionPoints = new ArrayList<>();

    public MeetingAnalysis(Long meetingId, AnalysisSource source) {
        this.meetingId = meetingId;
        this.source = source;
    }

    public void apply(String summary, AnalysisSource source,
                      List<String> decisions, List<ActionPointItem> actionPoints) {
        this.summary = summary;
        this.source = source;
        this.decisions.clear();
        this.decisions.addAll(decisions);
        this.actionPoints.clear();
        this.actionPoints.addAll(actionPoints);
    }
}
