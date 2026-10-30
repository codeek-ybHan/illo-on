package com.illoon.ai;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
class DueDateTest {
    @Test void preservesExplicitPastAndLeapDates() {
        assertEquals(LocalDateTime.of(2024, 2, 29, 18, 0), AiService.parseDue("2024-02-29"));
        assertEquals(LocalDateTime.of(2025, 12, 31, 18, 0), AiService.parseDue("2025-12-31"));
    }
    @Test void preservesTimeAndSeconds() {
        assertEquals(LocalDateTime.of(2025, 1, 1, 9, 30, 45), AiService.parseDue("2025-01-01T09:30:45"));
    }
    @Test void invalidOrUnspecifiedDatesRemainUnspecified() {
        assertNull(AiService.parseDue("2025-02-29"));
        assertNull(AiService.parseDue("다음 주"));
        assertNull(AiService.parseDue(""));
        assertNull(AiService.parseDue(null));
    }
}
