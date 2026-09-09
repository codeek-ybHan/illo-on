package com.illoon.sprint;

import com.illoon.sprint.dto.SprintCreateRequest;
import com.illoon.sprint.dto.SprintResponse;
import com.illoon.sprint.dto.SprintUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Sprint", description = "Sprint CRUD (Task 배정은 PUT /api/tasks/{id})")
@RestController
@RequiredArgsConstructor
public class SprintController {

    private final SprintService sprintService;

    @Operation(summary = "프로젝트 Sprint 목록")
    @GetMapping("/api/projects/{projectId}/sprints")
    public List<SprintResponse> list(@AuthenticationPrincipal Long userId,
                                     @PathVariable Long projectId) {
        return sprintService.listByProject(projectId, userId);
    }

    @Operation(summary = "Sprint 생성 (ADMIN)")
    @PostMapping("/api/projects/{projectId}/sprints")
    @ResponseStatus(HttpStatus.CREATED)
    public SprintResponse create(@AuthenticationPrincipal Long userId,
                                 @PathVariable Long projectId,
                                 @Valid @RequestBody SprintCreateRequest request) {
        return sprintService.create(projectId, userId, request);
    }

    @Operation(summary = "Sprint 상세")
    @GetMapping("/api/sprints/{sprintId}")
    public SprintResponse get(@AuthenticationPrincipal Long userId,
                              @PathVariable Long sprintId) {
        return sprintService.get(sprintId, userId);
    }

    @Operation(summary = "Sprint 수정 (ADMIN)")
    @PutMapping("/api/sprints/{sprintId}")
    public SprintResponse update(@AuthenticationPrincipal Long userId,
                                 @PathVariable Long sprintId,
                                 @Valid @RequestBody SprintUpdateRequest request) {
        return sprintService.update(sprintId, userId, request);
    }

    @Operation(summary = "Sprint 삭제 (ADMIN) — 배정 Task 는 배정만 해제")
    @DeleteMapping("/api/sprints/{sprintId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Long userId,
                       @PathVariable Long sprintId) {
        sprintService.delete(sprintId, userId);
    }
}
