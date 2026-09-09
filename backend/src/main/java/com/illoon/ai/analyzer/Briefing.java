package com.illoon.ai.analyzer;

import java.util.List;

/**
 * LLM Structured Output 대상. LLM이 이 형태의 JSON을 채워 반환한다.
 * (기획서 §8-2)
 */
public record Briefing(
        String summary,
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
