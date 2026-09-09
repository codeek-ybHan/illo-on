package com.illoon.task.domain;

import com.illoon.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 기획서 §10-6 TASK. 프로젝트에 속하고, 생성 맥락 회의(meeting_id)와
 * 배정 Sprint(sprint_id)를 선택적으로 참조한다.
 */
@Getter
@Entity
@Table(name = "task")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Task extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "meeting_id")
    private Long meetingId;

    @Column(name = "sprint_id")
    private Long sprintId;

    @Column(name = "assignee_id")
    private Long assigneeId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TaskPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private TaskStatus status;

    @Builder
    private Task(Long projectId, Long meetingId, Long sprintId, Long assigneeId,
                String title, String description, LocalDateTime dueDate,
                TaskPriority priority, TaskStatus status) {
        this.projectId = projectId;
        this.meetingId = meetingId;
        this.sprintId = sprintId;
        this.assigneeId = assigneeId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority != null ? priority : TaskPriority.MEDIUM;
        this.status = status != null ? status : TaskStatus.TODO;
    }

    /** Sprint 배정/해제 (기획서 §5-8 — 별도 API 없이 처리) */
    public void assignSprint(Long sprintId) {
        this.sprintId = sprintId;
    }

    /** PUT — 전체 교체 (기획서 §11-4, 별도 status API 없음) */
    public void update(String title, String description, Long assigneeId, LocalDateTime dueDate,
                       TaskPriority priority, TaskStatus status, Long sprintId, Long meetingId) {
        if (title != null && !title.isBlank()) this.title = title;
        this.description = description;
        this.assigneeId = assigneeId;
        this.dueDate = dueDate;
        if (priority != null) this.priority = priority;
        if (status != null) this.status = status;
        this.sprintId = sprintId;
        this.meetingId = meetingId;
    }
}
