package com.illoon.project.dto;

import jakarta.validation.constraints.Size;

public record MemberUpdateRequest(
        @Size(max = 50, message = "직급/역할은 50자 이하여야 합니다.")
        String jobTitle
) {}
