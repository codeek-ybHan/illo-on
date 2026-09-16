package com.illoon.ai.analyzer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

            [overview] 이 회의가 무엇을 위한 것이었는지 15~40자 내외의 한 문장으로 압축.
              장황하게 늘어놓지 말고, 나중에 이 한 줄만 봐도 회의 성격이 파악되게.

            [highlights] 회의에서 다뤄진 안건을 빠짐없이 모두 불릿으로 — 단, 각 불릿은 압축해서 쓴다.
              - 개수 상한은 없다. 안건이 10개면 10개를 다 쓴다. 짧은 회의면 짧게, 안건이 많으면
                그만큼 나열 — 임의로 3~7개로 줄이거나 일부 안건 자체를 생략하지 말 것
              - 단, "안건을 빠짐없이" 다루는 것과 "각 불릿을 장황하게 쓰는 것"은 다르다.
                같은 안건 안에서 오간 배경·근거·중간 논의는 발언 순서대로 나열하지 말고
                결론 중심으로 한 불릿에 압축한다. 불릿 하나는 40자 내외를 넘기지 않는다
                (핵심 수치·키워드는 살리되, 부연 설명은 걷어낼 것)
              - 배경/문제 → 논의된 쟁점·근거(수치 포함) → 도달한 결론 순서로, 안건별로 묶어서 정리
              - 같은 내용을 두 번 쓰지 않는다(요약 대상이 아닌 잡담·인사말 등은 애초에 제외)
              - 각 불릿은 명사형 또는 짧은 문장. "~했습니다" 같은 늘어지는 어미 금지
              - "아래에서 확인하세요" 같은 안내 문구 절대 넣지 말 것
              - 대화를 그대로 옮기지 말고 요점만

            [decisions] "이 회의에서 무엇이 확정됐는가"에 대한 답. actionPoints(누가 무엇을 언제)와는
            다른 축이다 — 방향/방침/범위가 정해진 것이면 담당자·기한이 없어도 decisions에 넣는다.
              - 포함: "~로 하기로 했다/합의했다/확정했다/결정했다" 식으로 명시적으로 마무리된 사항,
                여러 대안 중 하나를 선택한 것, 진행/보류/취소를 정한 것
              - 제외: 단순 정보 공유, 질문, 제안만 되고 합의에 이르지 못한 것, 대화 중인 상태
              - 애매하면(발화자가 확정처럼 말했지만 다른 참석자 동의가 없었던 경우 등) 포함하지 말 것
              - 없으면 빈 배열. 있는데 누락시키는 것보다 없는데 지어내는 게 훨씬 나쁘다.

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

    private static final Map<DayOfWeek, String> KOREAN_DAYS = Map.of(
            DayOfWeek.MONDAY, "월", DayOfWeek.TUESDAY, "화", DayOfWeek.WEDNESDAY, "수",
            DayOfWeek.THURSDAY, "목", DayOfWeek.FRIDAY, "금", DayOfWeek.SATURDAY, "토",
            DayOfWeek.SUNDAY, "일");

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
            LocalDate today = LocalDate.now();
            return chatClient.prompt()
                    .user(u -> u.text("""
                            오늘은 {today} ({dayOfWeek}요일, yyyy-MM-dd) 이다.
                            "다음 주 금요일", "이번 주말" 같은 상대 날짜는 이 요일을 기준으로 계산할 것.

                            [프로젝트 멤버 목록]
                            {members}

                            다음 회의 내용을 분석해줘:

                            {content}""")
                            .param("today", today.toString())
                            .param("dayOfWeek", KOREAN_DAYS.get(today.getDayOfWeek()))
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
