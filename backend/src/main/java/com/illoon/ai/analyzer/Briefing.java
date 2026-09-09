package com.illoon.ai.analyzer;

import java.util.List;

/**
 * LLM Structured Output 대상. (기획서 §8-2)
 */
public record Briefing(
        /** 한 줄 요약 ("한눈에 보기") */
        String overview,
        /** 회의 흐름·핵심 논의·결론 중심의 불릿 (군더더기 없이) */
        List<String> highlights,
        /** 확정된 결정사항 */
        List<String> decisions,
        List<ActionPoint> actionPoints
) {
    public record ActionPoint(
            String title,
            /** 담당자 후보 이름. 판단 불가 시 null */
            String assignee,
            /** ISO 날짜 문자열(yyyy-MM-dd). 판단 불가 시 null */
            String dueDate,
            /** HIGH | MEDIUM | LOW */
            String priority
    ) {}
}
