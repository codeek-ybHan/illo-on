package com.illoon.feedback.dto;

import com.illoon.feedback.domain.Feedback;

import java.time.LocalDateTime;

public record FeedbackResponse(
        Long feedbackId,
        Long userId,
        String authorName,
        String content,
        boolean resolved,
        LocalDateTime resolvedAt,
        LocalDateTime createdAt
) {
    public static FeedbackResponse of(Feedback feedback, String authorName) {
        return new FeedbackResponse(
                feedback.getId(),
                feedback.getUserId(),
                authorName,
                feedback.getContent(),
                feedback.isResolved(),
                feedback.getResolvedAt(),
                feedback.getCreatedAt());
    }
}
