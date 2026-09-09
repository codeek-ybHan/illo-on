package com.illoon.meeting.domain;

import com.illoon.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 기획서 §10-7 MEETING. content 는 회의 내용 / 메신저 대화 / STT 변환 결과를 담는다.
 */
@Getter
@Entity
@Table(name = "meeting")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Meeting extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_id")
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "text")
    private String content;

    private LocalDateTime meetingAt;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Builder
    private Meeting(Long projectId, String title, String content,
                    LocalDateTime meetingAt, Long createdBy) {
        this.projectId = projectId;
        this.title = title;
        this.content = content;
        this.meetingAt = meetingAt;
        this.createdBy = createdBy;
    }

    public void update(String title, String content, LocalDateTime meetingAt) {
        if (title != null && !title.isBlank()) this.title = title;
        this.content = content;
        this.meetingAt = meetingAt;
    }
}
