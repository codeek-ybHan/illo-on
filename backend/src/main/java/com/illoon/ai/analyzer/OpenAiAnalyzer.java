package com.illoon.ai.analyzer;

import com.illoon.common.exception.ApiException;
import com.illoon.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Spring AI + OpenAI. Structured Output(.entity)으로 Briefing JSON을 받는다. (기획서 §8-1, §8-2)
 * app.ai.provider=openai + OPENAI_API_KEY 필요.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "app.ai.provider", havingValue = "openai")
public class OpenAiAnalyzer implements AiAnalyzer {

    private static final String SYSTEM = """
            너는 회의록/메신저 대화를 분석하는 시니어 PM 어시스턴트다.
            군더더기 없이, 회의 흐름과 결론 중심으로 정리한다. 모든 출력은 한국어.

            [overview] 이 회의가 무엇을 위한 것이었는지 한 문장.

            [highlights] 회의 흐름을 따라간 핵심 불릿 3~7개.
              - 배경/문제 → 논의된 쟁점·근거(수치 포함) → 도달한 결론 순서
              - 각 불릿은 명사형 또는 짧은 문장. "~했습니다" 같은 늘어지는 어미 금지
              - "아래에서 확인하세요" 같은 안내 문구 절대 넣지 말 것
              - 대화를 그대로 옮기지 말고 요점만

            [decisions] 확정된 결정사항만 간결하게. 추측 금지. 없으면 빈 배열.

            [actionPoints] 실행해야 할 구체적 업무.
              - title: 대화 문장 금지. "무엇을 + 동작"의 간결한 명사구.
                예) "API 명세는 김민지가 9/15까지 작성하기로 했습니다" -> "API 명세서 작성"
              - assignee: 명확히 지목된 경우만 이름. 아니면 null (지어내지 말 것)
              - dueDate: 명확한 경우만 yyyy-MM-dd. 아니면 null.
                "다음 주 금요일", "9/15" 같은 표현은 아래 '오늘' 기준으로 계산. 연도를 임의로 넣지 말 것.
              - priority: HIGH | MEDIUM | LOW (긴급 시 HIGH, 여유 시 LOW, 기본 MEDIUM)
            """;

    private final ChatClient chatClient;

    public OpenAiAnalyzer(ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).defaultSystem(SYSTEM).build();
    }

    @Override
    public Briefing analyze(String meetingText) {
        try {
            return chatClient.prompt()
                    .user(u -> u.text("오늘은 {today} (yyyy-MM-dd) 이다.\n\n다음 회의 내용을 분석해줘:\n\n{content}")
                            .param("today", LocalDate.now().toString())
                            .param("content", meetingText))
                    .call()
                    .entity(Briefing.class);
        } catch (Exception e) {
            log.error("OpenAI analyze failed", e);
            throw new ApiException(ErrorCode.AI_ANALYZE_FAILED);
        }
    }
}
