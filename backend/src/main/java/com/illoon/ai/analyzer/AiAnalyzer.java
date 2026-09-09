package com.illoon.ai.analyzer;

/**
 * 회의 텍스트 → 구조화된 브리핑. 구현체는 provider 설정으로 선택된다.
 */
public interface AiAnalyzer {
    Briefing analyze(String meetingText);
}
