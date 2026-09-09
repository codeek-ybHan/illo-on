package com.illoon.ai.domain;

import com.illoon.task.domain.TaskPriority;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI가 추출한 Action Point (검토 전). 담당자/기한을 확정 못 하면 null.
 */
@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActionPointItem {

    @Column(name = "title", length = 300)
    private String title;

    /** 담당자 후보 이름 (자유 텍스트, 매칭은 프론트에서) */
    @Column(name = "assignee_hint", length = 50)
    private String assigneeHint;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 10)
    private TaskPriority priority;

    @Builder
    public ActionPointItem(String title, String assigneeHint, LocalDateTime dueDate, TaskPriority priority) {
        this.title = title;
        this.assigneeHint = assigneeHint;
        this.dueDate = dueDate;
        this.priority = priority != null ? priority : TaskPriority.MEDIUM;
    }
}
