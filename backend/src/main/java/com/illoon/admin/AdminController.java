package com.illoon.admin;

import com.illoon.admin.dto.AdminStatsResponse;
import com.illoon.admin.dto.AdminUserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin", description = "서비스 전역 관리자 전용 (isAdmin 계정만 접근 가능)")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "전체 사용자 목록 (관리자 전용)")
    @GetMapping("/users")
    public List<AdminUserResponse> users(@AuthenticationPrincipal Long userId) {
        return adminService.listUsers(userId);
    }

    @Operation(summary = "서비스 통계 (관리자 전용)")
    @GetMapping("/stats")
    public AdminStatsResponse stats(@AuthenticationPrincipal Long userId) {
        return adminService.getStats(userId);
    }

    @Operation(summary = "사용자 완전 삭제 (관리자 전용, 자기 자신 삭제 불가)")
    @DeleteMapping("/users/{targetUserId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@AuthenticationPrincipal Long userId, @PathVariable Long targetUserId) {
        adminService.deleteUser(userId, targetUserId);
    }
}
