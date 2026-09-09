package com.illoon.sprint;

import com.illoon.common.exception.ApiException;
import com.illoon.common.exception.ErrorCode;
import com.illoon.project.ProjectService;
import com.illoon.sprint.domain.Sprint;
import com.illoon.sprint.dto.SprintCreateRequest;
import com.illoon.sprint.dto.SprintResponse;
import com.illoon.sprint.dto.SprintUpdateRequest;
import com.illoon.task.TaskRepository;
import com.illoon.task.domain.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SprintService {

    private final SprintRepository sprintRepository;
    private final ProjectService projectService;
    private final TaskRepository taskRepository;

    @Transactional(readOnly = true)
    public List<SprintResponse> listByProject(Long projectId, Long userId) {
        projectService.requireMember(projectId, userId);
        return sprintRepository.findAllByProjectIdOrderByStartDateAscCreatedAtAsc(projectId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public SprintResponse get(Long sprintId, Long userId) {
        Sprint sprint = findSprint(sprintId);
        projectService.requireMember(sprint.getProjectId(), userId);
        return toResponse(sprint);
    }

    @Transactional
    public SprintResponse create(Long projectId, Long userId, SprintCreateRequest req) {
        projectService.requireAdmin(projectId, userId);
        Sprint sprint = sprintRepository.save(Sprint.builder()
                .projectId(projectId)
                .name(req.name())
                .startDate(req.startDate())
                .endDate(req.endDate())
                .build());
        return toResponse(sprint);
    }

    @Transactional
    public SprintResponse update(Long sprintId, Long userId, SprintUpdateRequest req) {
        Sprint sprint = findSprint(sprintId);
        projectService.requireAdmin(sprint.getProjectId(), userId);
        sprint.update(req.name(), req.startDate(), req.endDate(), req.status());
        return toResponse(sprint);
    }

    @Transactional
    public void delete(Long sprintId, Long userId) {
        Sprint sprint = findSprint(sprintId);
        projectService.requireAdmin(sprint.getProjectId(), userId);
        // 배정된 Task 는 삭제하지 않고 Sprint 배정만 해제
        taskRepository.findAllBySprintId(sprintId).forEach(t -> t.assignSprint(null));
        sprintRepository.delete(sprint);
    }

    private SprintResponse toResponse(Sprint s) {
        long total = taskRepository.countBySprintId(s.getId());
        long done = taskRepository.countBySprintIdAndStatus(s.getId(), TaskStatus.DONE);
        return SprintResponse.of(s, total, done);
    }

    private Sprint findSprint(Long sprintId) {
        return sprintRepository.findById(sprintId)
                .orElseThrow(() -> new ApiException(ErrorCode.SPRINT_NOT_FOUND));
    }
}
