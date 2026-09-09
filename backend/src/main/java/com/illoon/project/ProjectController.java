package com.illoon.project;

import com.illoon.project.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Project", description = "프로젝트 · 멤버 · 초대")
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "내 프로젝트 목록")
    @GetMapping
    public List<ProjectResponse> list(@AuthenticationPrincipal Long userId) {
        return projectService.listMyProjects(userId);
    }

    @Operation(summary = "프로젝트 생성")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse create(@AuthenticationPrincipal Long userId,
                                  @Valid @RequestBody ProjectCreateRequest request) {
        return projectService.create(userId, request);
    }

    @Operation(summary = "프로젝트 상세")
    @GetMapping("/{projectId}")
    public ProjectResponse get(@AuthenticationPrincipal Long userId,
                               @PathVariable Long projectId) {
        return projectService.get(projectId, userId);
    }

    @Operation(summary = "프로젝트 수정 (ADMIN)")
    @PutMapping("/{projectId}")
    public ProjectResponse update(@AuthenticationPrincipal Long userId,
                                  @PathVariable Long projectId,
                                  @Valid @RequestBody ProjectUpdateRequest request) {
        return projectService.update(projectId, userId, request);
    }

    @Operation(summary = "프로젝트 삭제 (ADMIN)")
    @DeleteMapping("/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Long userId,
                       @PathVariable Long projectId) {
        projectService.delete(projectId, userId);
    }

    @Operation(summary = "프로젝트 멤버 목록")
    @GetMapping("/{projectId}/members")
    public List<MemberResponse> members(@AuthenticationPrincipal Long userId,
                                        @PathVariable Long projectId) {
        return projectService.listMembers(projectId, userId);
    }

    @Operation(summary = "초대 링크 생성 (ADMIN)")
    @PostMapping("/{projectId}/invites")
    @ResponseStatus(HttpStatus.CREATED)
    public InviteResponse createInvite(@AuthenticationPrincipal Long userId,
                                       @PathVariable Long projectId) {
        return projectService.createInvite(projectId, userId);
    }
}
