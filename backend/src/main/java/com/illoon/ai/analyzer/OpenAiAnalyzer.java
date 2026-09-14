package com.illoon.ai.analyzer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring AI + OpenAI. Structured Output(.entity)으로 Briefing JSON을 받는다. (기획서 §8-1, §8-2)
 * app.ai.provider=openai + OPENAI_API_KEY 필요.
 */
@Slf4j
@Component
@Primary
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
              - assignee: 아래 [프로젝트 멤버 목록]에 있는 이름 중 명확히 지목된 사람만.
                목록에 없는 사람(고객사 담당자, 예시로 언급된 제3자 등)은 절대 넣지 말 것.
                지목된 사람이 목록에 없거나 불명확하면 null (지어내지 말 것)
              - dueDate: 명확한 경우만 yyyy-MM-dd. 아니면 null.
                "다음 주 금요일", "9/15" 같은 표현은 아래 '오늘' 기준으로 계산. 연도를 임의로 넣지 말 것.
              - priority: HIGH | MEDIUM | LOW (긴급 시 HIGH, 여유 시 LOW, 기본 MEDIUM)

            [예시 1 — 업무 회의]
            프로젝트 멤버: 김민지, 이승훈
            입력: "오늘 스프린트 회고 하겠습니다. 지난주 API 응답 속도가 800ms로 목표치 300ms보다
            느렸어요. 원인 분석해보니 쿼리에 인덱스가 안 걸려있더라고요. 민지가 인덱스 추가하는
            걸로 하죠. 이번 주 금요일까지 부탁드려요. 그리고 디자인 시안은 이번 스프린트에서
            확정하는 걸로 합의했습니다. 승훈님은 다음 배포 체크리스트 정리해주시고, 급한 건
            아니니 천천히 해주세요."
            출력:
              overview: "스프린트 회고 및 성능 개선/배포 준비 논의"
              highlights: ["API 응답 속도 800ms, 목표치 300ms 대비 지연", "원인은 쿼리 인덱스 미적용으로 확인", "디자인 시안은 이번 스프린트 내 확정하기로 합의"]
              decisions: ["디자인 시안 이번 스프린트 내 확정"]
              actionPoints:
                - title: "쿼리 인덱스 추가", assignee: "김민지", dueDate: "2025-06-13", priority: "MEDIUM"
                - title: "배포 체크리스트 정리", assignee: "이승훈", dueDate: null, priority: "LOW"

            [예시 2 — 잡담이 섞인 녹음본]
            녹음 전사는 주제 이탈·잡담이 섞이는 경우가 흔하다. 업무와 무관한 내용은
            highlights·decisions·actionPoints 어디에도 넣지 말고 조용히 무시한다.
            프로젝트 멤버: 이승훈
            입력: "어 일단 시작하죠. 저번에 얘기한 로그인 버그는 승훈님이 오늘 안에 고치기로
            했고요. 아 근데 주말에 뭐 하셨어요? 저는 등산 갔다왔는데 날씨가 너무 좋더라고요.
            네 뭐 아무튼, 다음 안건 없으면 오늘은 여기까지 할게요."
            출력:
              overview: "로그인 버그 수정 담당 확인 회의"
              highlights: ["로그인 버그 수정 담당자 확인"]
              decisions: []
              actionPoints:
                - title: "로그인 버그 수정", assignee: "이승훈", dueDate: "2025-06-10", priority: "HIGH"
              (※ 주말 등산 같은 잡담은 출력 어디에도 포함하지 않는다)
            """;

    private final ChatClient chatClient;
    private final MockAiAnalyzer fallback;

    public OpenAiAnalyzer(ChatModel chatModel, MockAiAnalyzer fallback) {
        this.chatClient = ChatClient.builder(chatModel).defaultSystem(SYSTEM).build();
        this.fallback = fallback;
    }

    @Override
    public Briefing analyze(String meetingText, List<String> memberNames) {
        try {
            String members = (memberNames == null || memberNames.isEmpty())
                    ? "(없음)" : String.join(", ", memberNames);
            return chatClient.prompt()
                    .user(u -> u.text("""
                            오늘은 {today} (yyyy-MM-dd) 이다.

                            [프로젝트 멤버 목록]
                            {members}

                            다음 회의 내용을 분석해줘:

                            {content}""")
                            .param("today", LocalDate.now().toString())
                            .param("members", members)
                            .param("content", meetingText))
                    .call()
                    .entity(Briefing.class);
        } catch (Exception e) {
            // OpenAI 장애·쿼터 초과 등 → 502 대신 규칙 기반 분석으로 degrade
            log.warn("OpenAI analyze failed, falling back to rule-based analyzer: {}", e.getMessage());
            return fallback.analyze(meetingText, memberNames);
        }
    }
}
