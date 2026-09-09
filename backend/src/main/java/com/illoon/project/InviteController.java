package com.illoon.project;

import com.illoon.project.dto.ProjectResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Invite", description = "초대 링크로 프로젝트 참여")
@RestController
@RequestMapping("/api/invites")
@RequiredArgsConstructor
public class InviteController {

    private final ProjectService projectService;

    @Operation(summary = "초대 링크로 프로젝트 참여")
    @PostMapping("/{token}/join")
    public ProjectResponse join(@AuthenticationPrincipal Long userId,
                                @PathVariable String token) {
        return projectService.joinByInvite(token, userId);
    }
}
