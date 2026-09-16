package com.illoon.admin;

import com.illoon.admin.dto.AdminStatsResponse;
import com.illoon.admin.dto.AdminUserResponse;
import com.illoon.common.exception.ApiException;
import com.illoon.common.exception.ErrorCode;
import com.illoon.feedback.FeedbackRepository;
import com.illoon.project.ProjectRepository;
import com.illoon.user.User;
import com.illoon.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final FeedbackRepository feedbackRepository;

    @Transactional(readOnly = true)
    public List<AdminUserResponse> listUsers(Long requesterId) {
        requireAdmin(requesterId);
        return userRepository.findAll().stream().map(AdminUserResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public AdminStatsResponse getStats(Long requesterId) {
        requireAdmin(requesterId);
        long totalFeedbacks = feedbackRepository.count();
        long resolvedFeedbacks = feedbackRepository.countByResolvedTrue();
        return new AdminStatsResponse(
                userRepository.count(),
                projectRepository.count(),
                totalFeedbacks,
                resolvedFeedbacks,
                totalFeedbacks - resolvedFeedbacks);
    }

    private void requireAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        if (!user.isAdmin()) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }
    }
}
