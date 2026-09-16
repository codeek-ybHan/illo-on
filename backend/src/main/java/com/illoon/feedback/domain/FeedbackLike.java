package com.illoon.feedback.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 피드백 메시지 공감("좋아요"). 복합 PK (feedback_id, user_id) — 사람당 한 번만 공감 가능.
 */
@Getter
@Entity
@Table(name = "feedback_like")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedbackLike {

    @EmbeddedId
    private Pk id;

    public FeedbackLike(Long feedbackId, Long userId) {
        this.id = new Pk(feedbackId, userId);
    }

    public Long getFeedbackId() {
        return id.feedbackId;
    }

    public Long getUserId() {
        return id.userId;
    }

    @Getter
    @Embeddable
    @EqualsAndHashCode
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Pk implements Serializable {
        @Column(name = "feedback_id")
        private Long feedbackId;

        @Column(name = "user_id")
        private Long userId;

        public Pk(Long feedbackId, Long userId) {
            this.feedbackId = feedbackId;
            this.userId = userId;
        }
    }
}
