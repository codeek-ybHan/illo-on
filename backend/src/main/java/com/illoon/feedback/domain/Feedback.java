package com.illoon.feedback.domain;

import com.illoon.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 앱 전체 공통 피드백 게시판 — 프로젝트와 무관하게 로그인한 모든 사용자에게 보인다.
 */
@Getter
@Entity
@Table(name = "feedback")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feedback extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private boolean resolved;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    /** 답장 대상 피드백 id (없으면 새 글). 별도 FK 매핑 없이 순수 참조 컬럼. */
    @Column(name = "reply_to_id")
    private Long replyToId;

    @Builder
    private Feedback(Long userId, String content, Long replyToId) {
        this.userId = userId;
        this.content = content;
        this.resolved = false;
        this.replyToId = replyToId;
    }

    public void resolve(boolean resolved) {
        this.resolved = resolved;
        this.resolvedAt = resolved ? LocalDateTime.now() : null;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
