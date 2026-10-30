package com.illoon.ai.analyzer;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
class MeetingDateTest {
    @Test void mockUsesMeetingDateInsteadOfExecutionDateAcrossYearEnd() {
        Briefing result = new MockAiAnalyzer().analyze("김민지가 내일까지 API 명세서를 작성하기로 했습니다.",
            List.of("김민지"), LocalDate.of(2024,12,31));
        assertFalse(result.actionPoints().isEmpty());
        assertEquals("2025-01-01", result.actionPoints().get(0).dueDate());
    }
}
