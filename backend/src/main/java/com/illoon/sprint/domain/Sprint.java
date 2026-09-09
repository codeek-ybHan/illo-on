package com.illoon.sprint.domain;

import com.illoon.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 기획서 §10-9 SPRINT. 프로젝트별로 생성하고 Task 를 배정한다.
 */
@Getter
@Entity
@Table(name = "sprint")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sprint extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sprint_id")
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(nullable = false, length = 100)
    private String name;

    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SprintStatus status;

    @Builder
    private Sprint(Long projectId, String name, LocalDate startDate, LocalDate endDate,
                   SprintStatus status) {
        this.projectId = projectId;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status != null ? status : SprintStatus.PLANNED;
    }

    public void update(String name, LocalDate startDate, LocalDate endDate, SprintStatus status) {
        if (name != null && !name.isBlank()) this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        if (status != null) this.status = status;
    }
}
