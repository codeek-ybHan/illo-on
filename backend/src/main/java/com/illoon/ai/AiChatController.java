package com.illoon.ai;

import com.illoon.ai.dto.AiChatReply;
import com.illoon.ai.dto.AiChatRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AI", description = "회의 AI 분석 · 브리핑 · 어시스턴트 대화")
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiChatController {

    private final AiChatService aiChatService;

    @Operation(summary = "AI 어시스턴트 대화 — 내 프로젝트·업무·회의 현황 기반 응답")
    @PostMapping("/chat")
    public AiChatReply chat(@AuthenticationPrincipal Long userId, @RequestBody AiChatRequest request) {
        return aiChatService.chat(userId, request);
    }
}
