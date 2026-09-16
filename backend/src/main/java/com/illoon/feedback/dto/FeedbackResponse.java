package com.illoon.feedback.dto;

import com.illoon.feedback.domain.Feedback;

import java.time.LocalDateTime;

/**
 * 익명 피드백 게시판 응답. 작성자 식별 정보(이름/이메일/userId)는 절대 내려주지 않는다 —
 * 관리자 메시지인지 여부(authorIsAdmin)와 뷰어 본인 글인지(isMine)만 구분한다.
 * isMine 은 뷰어 자신의 글에만 true 이므로 다른 사람에게 작성자를 노출하지 않는다.
 */
public record FeedbackResponse(
        Long feedbackId,
        boolean authorIsAdmin,
        boolean isMine,
        String content,
        boolean resolved,
        LocalDateTime resolvedAt,
        Long replyToId,
        long likeCount,
        boolean likedByMe,
        LocalDateTime createdAt
) {
    public static FeedbackResponse of(
            Feedback feedback, boolean authorIsAdmin, boolean isMine,
            long likeCount, boolean likedByMe) {
        return new FeedbackResponse(
                feedback.getId(),
                authorIsAdmin,
                isMine,
                feedback.getContent(),
                feedback.isResolved(),
                feedback.getResolvedAt(),
                feedback.getReplyToId(),
                likeCount,
                likedByMe,
                feedback.getCreatedAt());
    }
}
