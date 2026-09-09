package com.illoon.task;

import com.illoon.common.exception.ApiException;
import com.illoon.common.exception.ErrorCode;
import com.illoon.project.ProjectService;
import com.illoon.project.domain.Project;
import com.illoon.project.repository.ProjectMemberRepository;
import com.illoon.project.repository.ProjectRepository;
import com.illoon.task.domain.Task;
import com.illoon.task.dto.TaskCreateRequest;
import com.illoon.task.dto.TaskResponse;
import com.illoon.task.dto.TaskUpdateRequest;
import com.illoon.user.User;
import com.illoon.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;

    // ---------- queries ----------

    @Transactional(readOnly = true)
    public List<TaskResponse> listByProject(Long projectId, Long sprintId, Long userId) {
        projectService.requireMember(projectId, userId);
        List<Task> tasks = sprintId == null
                ? taskRepository.findAllByProjectIdOrderByCreatedAtDesc(projectId)
                : taskRepository.findAllByProjectIdAndSprintIdOrderByCreatedAtDesc(projectId, sprintId);
        return toResponses(tasks);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> listMine(Long userId) {
        List<Task> tasks = taskRepository.findAllByAssigneeId(userId);
        tasks.sort(Comparator.comparing(Task::getDueDate,
                Comparator.nullsLast(Comparator.naturalOrder())));
        return toResponses(tasks);
    }

    @Transactional(readOnly = true)
    public TaskResponse get(Long taskId, Long userId) {
        Task task = findTask(taskId);
        projectService.requireMember(task.getProjectId(), userId);
        return toResponses(List.of(task)).get(0);
    }

    // ---------- commands ----------

    @Transactional
    public TaskResponse create(Long userId, TaskCreateRequest req) {
        projectService.requireMember(req.projectId(), userId);
        validateAssignee(req.projectId(), req.assigneeId());

        Task task = taskRepository.save(Task.builder()
                .projectId(req.projectId())
                .meetingId(req.meetingId())
                .sprintId(req.sprintId())
                .assigneeId(req.assigneeId())
                .title(req.title())
                .description(req.description())
                .dueDate(req.dueDate())
                .priority(req.priority())
                .build());
        return toResponses(List.of(task)).get(0);
    }

    @Transactional
    public TaskResponse update(Long taskId, Long userId, TaskUpdateRequest req) {
        Task task = findTask(taskId);
        projectService.requireMember(task.getProjectId(), userId);
        validateAssignee(task.getProjectId(), req.assigneeId());

        task.update(req.title(), req.description(), req.assigneeId(), req.dueDate(),
                req.priority(), req.status(), req.sprintId(), req.meetingId());
        return toResponses(List.of(task)).get(0);
    }

    @Transactional
    public void delete(Long taskId, Long userId) {
        Task task = findTask(taskId);
        projectService.requireMember(task.getProjectId(), userId);
        taskRepository.delete(task);
    }

    // ---------- helpers ----------

    private void validateAssignee(Long projectId, Long assigneeId) {
        if (assigneeId != null
                && !projectMemberRepository.existsByIdProjectIdAndIdUserId(projectId, assigneeId)) {
            throw new ApiException(ErrorCode.INVALID_INPUT, "담당자는 프로젝트 멤버여야 합니다.");
        }
    }

    private Task findTask(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ApiException(ErrorCode.TASK_NOT_FOUND));
    }

    private List<TaskResponse> toResponses(List<Task> tasks) {
        if (tasks.isEmpty()) return List.of();

        Map<Long, String> projectNames = projectRepository.findAllById(
                        tasks.stream().map(Task::getProjectId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(Project::getId, Project::getName));

        Map<Long, String> assigneeNames = userRepository.findAllById(
                        tasks.stream().map(Task::getAssigneeId).filter(java.util.Objects::nonNull)
                                .collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(User::getId, User::getName));

        return tasks.stream()
                .map(t -> TaskResponse.of(t,
                        projectNames.get(t.getProjectId()),
                        t.getAssigneeId() == null ? null : assigneeNames.get(t.getAssigneeId())))
                .toList();
    }
}
