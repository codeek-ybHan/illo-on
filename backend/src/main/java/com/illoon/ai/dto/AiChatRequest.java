package com.illoon.ai.dto;

import java.util.List;

/**
 * AI 어시스턴트 대화 요청.
 * history: 직전 대화 (role = "user" | "assistant"). 최근 몇 턴만 보내면 된다.
 */
public record AiChatRequest(String message, List<Turn> history) {

    public record Turn(String role, String content) {}
}
