package com.illoon.task;

import com.illoon.task.dto.TaskCreateRequest;
import com.illoon.task.dto.TaskResponse;
import com.illoon.task.dto.TaskUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Task", description = "업무 CRUD + 내 업무")
@RestController
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "프로젝트 Task 목록 (sprintId 로 Sprint 내 Task 필터)")
    @GetMapping("/api/tasks")
    public List<TaskResponse> list(@AuthenticationPrincipal Long userId,
                                   @RequestParam Long projectId,
                                   @RequestParam(required = false) Long sprintId) {
        return taskService.listByProject(projectId, sprintId, userId);
    }

    @Operation(summary = "내 Task (담당자 = 나)")
    @GetMapping("/api/me/tasks")
    public List<TaskResponse> myTasks(@AuthenticationPrincipal Long userId) {
        return taskService.listMine(userId);
    }

    @Operation(summary = "Task 생성")
    @PostMapping("/api/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@AuthenticationPrincipal Long userId,
                               @Valid @RequestBody TaskCreateRequest request) {
        return taskService.create(userId, request);
    }

    @Operation(summary = "Task 상세")
    @GetMapping("/api/tasks/{taskId}")
    public TaskResponse get(@AuthenticationPrincipal Long userId,
                            @PathVariable Long taskId) {
        return taskService.get(taskId, userId);
    }

    @Operation(summary = "Task 수정 (상태 변경 · Sprint 배정 포함)")
    @PutMapping("/api/tasks/{taskId}")
    public TaskResponse update(@AuthenticationPrincipal Long userId,
                               @PathVariable Long taskId,
                               @Valid @RequestBody TaskUpdateRequest request) {
        return taskService.update(taskId, userId, request);
    }

    @Operation(summary = "Task 삭제")
    @DeleteMapping("/api/tasks/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Long userId,
                       @PathVariable Long taskId) {
        taskService.delete(taskId, userId);
    }
}
