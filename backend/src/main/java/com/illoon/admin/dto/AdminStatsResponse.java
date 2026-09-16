package com.illoon.admin.dto;

public record AdminStatsResponse(
        long totalUsers,
        long totalProjects,
        long totalFeedbacks,
        long resolvedFeedbacks,
        long pendingFeedbacks
) {}
