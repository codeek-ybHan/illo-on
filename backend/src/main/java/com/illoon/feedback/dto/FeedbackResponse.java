package com.illoon.feedback.dto;

import com.illoon.feedback.domain.Feedback;

import java.time.LocalDateTime;

/**
 * 익명 피드백 게시판 응답. 작성자 식별 정보(이름/이메일/userId)는 절대 내려주지 않는다 —
 * 관리자 메시지인지 여부(authorIsAdmin)만 구분한다.
 */
public record FeedbackResponse(
        Long feedbackId,
        boolean authorIsAdmin,
        String content,
        boolean resolved,
        LocalDateTime resolvedAt,
        Long replyToId,
        long likeCount,
        boolean likedByMe,
        LocalDateTime createdAt
) {
    public static FeedbackResponse of(
            Feedback feedback, boolean authorIsAdmin, long likeCount, boolean likedByMe) {
        return new FeedbackResponse(
                feedback.getId(),
                authorIsAdmin,
                feedback.getContent(),
                feedback.isResolved(),
                feedback.getResolvedAt(),
                feedback.getReplyToId(),
                likeCount,
                likedByMe,
                feedback.getCreatedAt());
    }
}
