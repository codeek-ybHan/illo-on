package com.illoon.feedback;

import com.illoon.feedback.dto.FeedbackCreateRequest;
import com.illoon.feedback.dto.FeedbackResolveRequest;
import com.illoon.feedback.dto.FeedbackResponse;
import com.illoon.feedback.dto.FeedbackUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Feedback", description = "앱 피드백 게시판 (전역, 프로젝트 무관)")
@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @Operation(summary = "피드백 목록 (전체 사용자 공통, 익명)")
    @GetMapping
    public List<FeedbackResponse> list(@AuthenticationPrincipal Long userId) {
        return feedbackService.list(userId);
    }

    @Operation(summary = "피드백 작성 (답장이면 replyToId 포함)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FeedbackResponse create(@AuthenticationPrincipal Long userId,
                                   @Valid @RequestBody FeedbackCreateRequest request) {
        return feedbackService.create(userId, request.content(), request.replyToId());
    }

    @Operation(summary = "피드백 내용 수정 (작성자 본인만 가능)")
    @PatchMapping("/{feedbackId}")
    public FeedbackResponse update(@AuthenticationPrincipal Long userId,
                                   @PathVariable Long feedbackId,
                                   @Valid @RequestBody FeedbackUpdateRequest request) {
        return feedbackService.update(feedbackId, userId, request.content());
    }

    @Operation(summary = "피드백 삭제 (관리자 계정만 가능)")
    @DeleteMapping("/{feedbackId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Long userId, @PathVariable Long feedbackId) {
        feedbackService.delete(feedbackId, userId);
    }

    @Operation(summary = "반영완료 토글 (관리자 계정만 가능)")
    @PatchMapping("/{feedbackId}/resolve")
    public FeedbackResponse resolve(@AuthenticationPrincipal Long userId,
                                    @PathVariable Long feedbackId,
                                    @Valid @RequestBody FeedbackResolveRequest request) {
        return feedbackService.setResolved(feedbackId, userId, request.resolved());
    }

    @Operation(summary = "공감 토글")
    @PostMapping("/{feedbackId}/like")
    public FeedbackResponse toggleLike(@AuthenticationPrincipal Long userId,
                                       @PathVariable Long feedbackId) {
        return feedbackService.toggleLike(feedbackId, userId);
    }
}
