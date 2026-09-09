package com.illoon.ai.dto;

/**
 * AI 어시스턴트 응답.
 * provider: "openai" = 실제 LLM 응답, "mock" = 설정 안내(대화 비활성).
 */
public record AiChatReply(String reply, String provider) {}
