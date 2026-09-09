package com.illoon.project.domain;

import com.illoon.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 기획서 §10-3 PROJECT. 업무·회의·Sprint 를 묶는 단위.
 */
@Getter
@Entity
@Table(name = "project")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProjectStatus status;

    @Builder
    private Project(Long teamId, String name, String description,
                    LocalDate startDate, LocalDate endDate, ProjectStatus status) {
        this.teamId = teamId;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status != null ? status : ProjectStatus.PLANNED;
    }

    public void update(String name, String description,
                       LocalDate startDate, LocalDate endDate, ProjectStatus status) {
        if (name != null && !name.isBlank()) this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        if (status != null) this.status = status;
    }
}
