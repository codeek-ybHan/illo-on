package com.illoon.ai.analyzer;

import com.illoon.common.exception.ApiException;
import com.illoon.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Spring AI + OpenAI. Structured Output(.entity)으로 Briefing JSON을 받는다. (기획서 §8-1, §8-2)
 * app.ai.provider=openai + OPENAI_API_KEY 필요.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "app.ai.provider", havingValue = "openai")
public class OpenAiAnalyzer implements AiAnalyzer {

    private static final String SYSTEM = """
            너는 회의록/메신저 대화를 분석해 실행 가능한 업무를 뽑아내는 어시스턴트다.
            반드시 아래 규칙을 지켜라.
            - summary: 회의의 핵심을 2~3문장으로 요약 (한국어)
            - decisions: 확정된 결정사항만. 추측 금지. 없으면 빈 배열
            - actionPoints: 실행해야 할 구체적 업무. 각 항목은 title 필수.
              assignee: 담당자가 명확히 언급된 경우에만 이름. 아니면 null
              dueDate: 기한이 명확한 경우에만 yyyy-MM-dd. 아니면 null
              priority: HIGH | MEDIUM | LOW 중 하나 (기본 MEDIUM)
            - 담당자나 기한을 지어내지 마라. 불확실하면 null.
            """;

    private final ChatClient chatClient;

    public OpenAiAnalyzer(ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).defaultSystem(SYSTEM).build();
    }

    @Override
    public Briefing analyze(String meetingText) {
        try {
            return chatClient.prompt()
                    .user(u -> u.text("다음 회의 내용을 분석해줘:\n\n{content}")
                            .param("content", meetingText))
                    .call()
                    .entity(Briefing.class);
        } catch (Exception e) {
            log.error("OpenAI analyze failed", e);
            throw new ApiException(ErrorCode.AI_ANALYZE_FAILED);
        }
    }
}
