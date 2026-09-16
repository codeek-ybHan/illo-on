package com.illoon.feedback.dto;

import jakarta.validation.constraints.NotNull;

public record FeedbackResolveRequest(
        @NotNull(message = "resolved 값이 필요합니다.")
        Boolean resolved
) {}
