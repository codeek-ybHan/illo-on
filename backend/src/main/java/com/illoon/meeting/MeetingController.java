package com.illoon.meeting;

import com.illoon.meeting.dto.MeetingCreateRequest;
import com.illoon.meeting.dto.MeetingDetailResponse;
import com.illoon.meeting.dto.MeetingResponse;
import com.illoon.meeting.dto.MeetingUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Meeting", description = "회의 CRUD (AI 분석은 Phase 6)")
@RestController
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @Operation(summary = "프로젝트 회의 목록")
    @GetMapping("/api/projects/{projectId}/meetings")
    public List<MeetingResponse> list(@AuthenticationPrincipal Long userId,
                                      @PathVariable Long projectId) {
        return meetingService.listByProject(projectId, userId);
    }

    @Operation(summary = "내 회의 (전 프로젝트, from/to 날짜 필터)")
    @GetMapping("/api/me/meetings")
    public List<MeetingResponse> myMeetings(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false)
            @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
            java.time.LocalDate from,
            @RequestParam(required = false)
            @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
            java.time.LocalDate to) {
        return meetingService.listMine(userId, from, to);
    }

    @Operation(summary = "회의 생성")
    @PostMapping("/api/projects/{projectId}/meetings")
    @ResponseStatus(HttpStatus.CREATED)
    public MeetingDetailResponse create(@AuthenticationPrincipal Long userId,
                                        @PathVariable Long projectId,
                                        @Valid @RequestBody MeetingCreateRequest request) {
        return meetingService.create(projectId, userId, request);
    }

    @Operation(summary = "회의 상세")
    @GetMapping("/api/meetings/{meetingId}")
    public MeetingDetailResponse get(@AuthenticationPrincipal Long userId,
                                     @PathVariable Long meetingId) {
        return meetingService.get(meetingId, userId);
    }

    @Operation(summary = "회의 수정")
    @PutMapping("/api/meetings/{meetingId}")
    public MeetingDetailResponse update(@AuthenticationPrincipal Long userId,
                                        @PathVariable Long meetingId,
                                        @Valid @RequestBody MeetingUpdateRequest request) {
        return meetingService.update(meetingId, userId, request);
    }

    @Operation(summary = "회의 삭제")
    @DeleteMapping("/api/meetings/{meetingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Long userId,
                       @PathVariable Long meetingId) {
        meetingService.delete(meetingId, userId);
    }
}
