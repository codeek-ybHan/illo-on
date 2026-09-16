package com.illoon.admin;

import com.illoon.admin.dto.AdminStatsResponse;
import com.illoon.admin.dto.AdminUserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
