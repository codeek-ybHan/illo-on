package com.illoon.team;

import com.illoon.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 기획서 §10-2 TEAM. MVP 에서는 프로젝트 생성 시 자동 생성되는 컨테이너로만 쓴다
 * (팀 관리 UI 없음). 스키마는 향후 팀당 다중 프로젝트를 지원한다.
 */
@Getter
@Entity
@Table(name = "team")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Builder
    private Team(String name) {
        this.name = name;
    }
}
