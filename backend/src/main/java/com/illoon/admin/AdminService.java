package com.illoon.admin;

import com.illoon.admin.dto.AdminStatsResponse;
import com.illoon.admin.dto.AdminUserResponse;
import com.illoon.common.exception.ApiException;
import com.illoon.common.exception.ErrorCode;
import com.illoon.feedback.FeedbackRepository;
import com.illoon.meeting.MeetingMemberRepository;
import com.illoon.project.ProjectMemberRepository;
import com.illoon.project.ProjectRepository;
import com.illoon.task.TaskRepository;
import com.illoon.task.domain.Task;
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
    private final ProjectMemberRepository projectMemberRepository;
    private final MeetingMemberRepository meetingMemberRepository;
    private final TaskRepository taskRepository;

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

    /**
     * 사용자 완전 삭제. 이 사용자로 인한 다른 테이블의 참조만 정리하고(멤버십 삭제,
     * 담당 Task 배정 해제), 다른 사람의 프로젝트·Task·회의·피드백 자체는 건드리지 않는다.
     */
    @Transactional
    public void deleteUser(Long requesterId, Long targetUserId) {
        requireAdmin(requesterId);
        if (requesterId.equals(targetUserId)) {
            throw new ApiException(ErrorCode.INVALID_INPUT, "자기 자신은 삭제할 수 없습니다.");
        }
        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        for (Task task : taskRepository.findAllByAssigneeId(targetUserId)) {
            task.unassign();
        }
        projectMemberRepository.deleteAllByIdUserId(targetUserId);
        meetingMemberRepository.deleteAllByIdUserId(targetUserId);

        userRepository.delete(target);
    }

    private void requireAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        if (!user.isAdmin()) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }
    }
}
