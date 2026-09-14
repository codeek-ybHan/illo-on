package com.illoon.ai.analyzer;

import java.util.List;

/**
 * 회의 텍스트 → 구조화된 브리핑. 구현체는 provider 설정으로 선택된다.
 */
public interface AiAnalyzer {
    /**
     * @param memberNames 프로젝트 멤버 이름 목록 — Action Point 담당자 추정을 이 안으로 제한한다.
     */
    Briefing analyze(String meetingText, List<String> memberNames);
}
