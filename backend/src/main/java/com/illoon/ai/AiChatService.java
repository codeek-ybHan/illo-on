package com.illoon.ai;

import com.illoon.ai.dto.AiChatReply;
import com.illoon.ai.dto.AiChatRequest;
import com.illoon.board.BoardService;
import com.illoon.board.dto.BoardResponse;
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
 * 우측 AI 패널의 대화. 사용자의 프로젝트·Sprint·업무·회의 현황을 컨텍스트로 주입한다.
 * provider=openai 일 때만 실제 LLM 호출. 그 외에는 설정 안내를 반환(대화 비활성).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatService {

    private static final String SYSTEM = """
            너는 일로ON(업무관리 + AI 회의록 SaaS)의 어시스턴트다.
            아래 [사용자 현황]과 대화 맥락을 바탕으로 한국어로 간결하게 답한다.
            - 표 대신 짧은 불릿. 군더더기 없이.
            - 현황에 없는 내용은 지어내지 말고 모른다고 한다.
            - 사용자가 업무·회의 생성을 원하면 방법을 안내하되, 직접 만들지는 못한다고 말한다.
            """;

    private static final int MAX_HISTORY_TURNS = 8;

    private final ObjectProvider<ChatModel> chatModelProvider;
    private final BoardService boardService;

    @Value("${app.ai.provider}")
    private String provider;

    public AiChatReply chat(Long userId, AiChatRequest req) {
        String message = req == null ? null : req.message();
        if (message == null || message.isBlank()) {
            return new AiChatReply("무엇을 도와드릴까요? 회의·업무·일정에 대해 물어보세요.", provider());
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
            String context = renderContext(boardService.getBoard(userId));

            List<Message> messages = new ArrayList<>();
            List<AiChatRequest.Turn> history = req.history() == null ? List.of() : req.history();
            history.stream()
                    .skip(Math.max(0, history.size() - MAX_HISTORY_TURNS))
                    .forEach(t -> messages.add("assistant".equalsIgnoreCase(t.role())
                            ? new AssistantMessage(t.content())
                            : new UserMessage(t.content())));
            messages.add(new UserMessage(message));

            String reply = ChatClient.create(model).prompt()
                    .system(SYSTEM + "\n\n[사용자 현황]\n" + context)
                    .messages(messages)
                    .call()
                    .content();
            return new AiChatReply(reply == null || reply.isBlank() ? "..." : reply, "openai");
        } catch (Exception e) {
            log.warn("AI chat failed: {}", e.getMessage());
            return new AiChatReply("지금은 AI 응답을 가져올 수 없어요. 잠시 후 다시 시도해 주세요.", "openai");
        }
    }

    private String provider() {
        return "openai".equalsIgnoreCase(provider) ? "openai" : "mock";
    }

    private String renderContext(BoardResponse b) {
        StringBuilder sb = new StringBuilder();

        sb.append("· 열린 내 업무 ").append(b.openTaskCount()).append("건")
                .append(" (3일 내 마감 ").append(b.dueSoonCount())
                .append(", 마감 지남 ").append(b.overdueCount())
                .append(", 오늘 마감 ").append(b.todayTaskCount()).append(")\n");
        sb.append("· 예정 회의 ").append(b.upcomingMeetingCount()).append("건\n");

        if (!b.projects().isEmpty()) {
            sb.append("· 프로젝트:\n");
            b.projects().forEach(p -> sb.append("   - ").append(p.name())
                    .append(" (").append(p.status())
                    .append(", 진행률 ").append(p.progress()).append("%")
                    .append(", 업무 ").append(p.taskCount()).append(")\n"));
        }
        if (!b.activeSprints().isEmpty()) {
            sb.append("· 활성 Sprint:\n");
            b.activeSprints().forEach(s -> sb.append("   - ").append(s.projectName())
                    .append(" / ").append(s.name())
                    .append(" (마감 ").append(s.endDate())
                    .append(", ").append(s.doneCount()).append("/").append(s.taskCount())
                    .append(" 완료)\n"));
        }
        if (!b.todayMeetings().isEmpty()) {
            sb.append("· 오늘 회의:\n");
            b.todayMeetings().forEach(m -> sb.append("   - ").append(m.title())
                    .append(m.meetingAt() != null ? " (" + m.meetingAt().toLocalTime() + ")" : "")
                    .append("\n"));
        }
        if (!b.todayTasks().isEmpty()) {
            sb.append("· 오늘 마감 업무:\n");
            b.todayTasks().forEach(t -> sb.append("   - ").append(t.title()).append("\n"));
        }
        return sb.toString();
    }
}
