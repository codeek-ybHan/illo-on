package com.illoon.ai;

import com.illoon.ai.dto.AiChatReply;
import com.illoon.ai.dto.AiChatRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 우측 AI 패널의 대화. 사용자의 프로젝트·Sprint·업무 현황 + 회의 기록(요약·결정사항·원문)을
 * 컨텍스트로 주입한다. provider=openai 일 때만 실제 LLM 호출.
 *
 * <p>컨텍스트는 {@link AiChatContext} 가 짧은 트랜잭션으로 문자열화하고,
 * 느린 LLM 호출은 여기서 트랜잭션 밖에서 수행한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatService {

    private static final String SYSTEM = """
            너는 일로ON(업무관리 + AI 회의록 SaaS)의 어시스턴트다.
            아래 [사용자 현황]과 [회의 기록], 그리고 대화 맥락을 근거로 한국어로 답한다.
            - "지난 회의에서 뭐라고 했지?" 같은 질문은 [회의 기록]의 요약·결정·할일·내용을 찾아 답한다.
            - 어느 회의 내용인지 물으면 회의 제목과 날짜를 함께 언급한다.
            - 표 대신 짧은 불릿. 군더더기 없이.
            - [회의 기록]/[사용자 현황]에 없는 내용은 지어내지 말고 "기록에 없다"고 한다.
            - 사용자가 업무·회의 생성을 원하면 방법을 안내하되, 직접 만들지는 못한다고 말한다.
            """;

    private static final int MAX_HISTORY_TURNS = 8;

    private final ObjectProvider<ChatModel> chatModelProvider;
    private final AiChatContext context;

    @Value("${app.ai.provider}")
    private String provider;

    public AiChatReply chat(Long userId, AiChatRequest req) {
        String message = req == null ? null : req.message();
        if (message == null || message.isBlank()) {
            return new AiChatReply("무엇을 도와드릴까요? 회의·업무·일정에 대해 물어보세요.", providerLabel());
        }
        if (!"openai".equalsIgnoreCase(provider)) {
            return new AiChatReply(
                    "AI 대화는 OpenAI 설정이 필요합니다. backend/config/local.properties 에 "
                            + "app.ai.provider=openai 와 API 키를 넣고 백엔드를 재시작하세요.",
                    "mock");
        }
        ChatModel model = chatModelProvider.getIfAvailable();
        if (model == null) {
            return new AiChatReply("AI 모델이 초기화되지 않았습니다.", "mock");
        }

        try {
            String ctx = context.render(userId); // 짧은 트랜잭션 안에서 문자열화

            List<Message> messages = new ArrayList<>();
            List<AiChatRequest.Turn> history = req.history() == null ? List.of() : req.history();
            history.stream()
                    .skip(Math.max(0, history.size() - MAX_HISTORY_TURNS))
                    .forEach(t -> messages.add("assistant".equalsIgnoreCase(t.role())
                            ? new AssistantMessage(t.content())
                            : new UserMessage(t.content())));
            messages.add(new UserMessage(message));

            String reply = ChatClient.create(model).prompt()
                    .system(SYSTEM + "\n\n" + ctx)
                    .messages(messages)
                    .call()
                    .content();
            return new AiChatReply(reply == null || reply.isBlank() ? "..." : reply, "openai");
        } catch (Exception e) {
            log.warn("AI chat failed: {}", e.getMessage());
            return new AiChatReply("지금은 AI 응답을 가져올 수 없어요. 잠시 후 다시 시도해 주세요.", "openai");
        }
    }

    private String providerLabel() {
        return "openai".equalsIgnoreCase(provider) ? "openai" : "mock";
    }
}
