package com.illoon.feedback;

import com.illoon.feedback.dto.FeedbackCreateRequest;
import com.illoon.feedback.dto.FeedbackResolveRequest;
import com.illoon.feedback.dto.FeedbackResponse;
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

    @Operation(summary = "피드백 목록 (전체 사용자 공통)")
    @GetMapping
    public List<FeedbackResponse> list() {
        return feedbackService.list();
    }

    @Operation(summary = "피드백 작성")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FeedbackResponse create(@AuthenticationPrincipal Long userId,
                                   @Valid @RequestBody FeedbackCreateRequest request) {
        return feedbackService.create(userId, request.content());
    }

    @Operation(summary = "반영완료 토글 (작성자 계정만 가능)")
    @PatchMapping("/{feedbackId}/resolve")
    public FeedbackResponse resolve(@AuthenticationPrincipal Long userId,
                                    @PathVariable Long feedbackId,
                                    @Valid @RequestBody FeedbackResolveRequest request) {
        return feedbackService.setResolved(feedbackId, userId, request.resolved());
    }
}
