package com.illoon.user;

import com.illoon.user.dto.PasswordChangeRequest;
import com.illoon.user.dto.ProfileUpdateRequest;
import com.illoon.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "내 계정 정보")
@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "계정 정보 수정 (이름/이메일)")
    @PatchMapping
    public UserResponse updateProfile(@AuthenticationPrincipal Long userId,
                                      @Valid @RequestBody ProfileUpdateRequest request) {
        return userService.updateProfile(userId, request.name(), request.email());
    }

    @Operation(summary = "비밀번호 변경")
    @PatchMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@AuthenticationPrincipal Long userId,
                               @Valid @RequestBody PasswordChangeRequest request) {
        userService.changePassword(userId, request.currentPassword(), request.newPassword());
    }
}
