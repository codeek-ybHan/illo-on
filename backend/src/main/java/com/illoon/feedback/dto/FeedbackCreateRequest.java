package com.illoon.feedback.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FeedbackCreateRequest(
        @NotBlank(message = "내용을 입력해 주세요.")
        @Size(max = 1000, message = "내용은 1000자 이하여야 합니다.")
        String content
) {}
