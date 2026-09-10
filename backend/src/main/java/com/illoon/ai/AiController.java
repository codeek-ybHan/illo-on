package com.illoon.ai;

import com.illoon.ai.dto.BriefingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "AI", description = "회의 AI 분석 · 브리핑 · 어시스턴트 대화")
@RestController
@RequestMapping("/api/meetings/{meetingId}")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @Operation(summary = "회의 분석 — audio 파일이 있으면 STT 후 분석, 없으면 회의 내용 분석")
    @PostMapping(path = "/analyze",
            consumes = {"multipart/form-data", "application/json", "application/octet-stream"})
    public BriefingResponse analyze(@AuthenticationPrincipal Long userId,
                                    @PathVariable Long meetingId,
                                    @RequestParam(value = "audio", required = false) MultipartFile audio) {
        return aiService.analyze(meetingId, userId, audio);
    }

    @Operation(summary = "회의 브리핑 결과 조회")
    @GetMapping("/summary")
    public BriefingResponse summary(@AuthenticationPrincipal Long userId,
                                    @PathVariable Long meetingId) {
        return aiService.getSummary(meetingId, userId);
    }
}
