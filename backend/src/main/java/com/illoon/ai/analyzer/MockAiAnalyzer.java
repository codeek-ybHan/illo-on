package com.illoon.ai.analyzer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * OpenAI 키 없이 데모/개발용. 규칙 기반으로 결정사항·Action Point 를 추출한다.
 * 실제 품질은 OpenAiAnalyzer 담당. (app.ai.provider=mock, 기본값)
 */
@Component
@ConditionalOnProperty(name = "app.ai.provider", havingValue = "mock", matchIfMissing = true)
public class MockAiAnalyzer implements AiAnalyzer {

    private static final Pattern SENTENCE = Pattern.compile("[^.!?\\n。]+[.!?。]?");
    // "홍길동가/이/께서" 또는 "홍길동 님/대리 …" 처럼 이름+조사가 붙어 있는 경우만 인정
    private static final Pattern NAME = Pattern.compile(
            "([가-힣]{2,4})(?:님|씨|대리|과장|차장|부장|팀장|매니저|리드)?(?:가|이|께서)(?![가-힣])");
    private static final java.util.Set<String> NON_NAMES = java.util.Set.of(
            "우리", "저희", "제가", "내가", "네가", "누가", "모두", "각자", "본인", "담당자");
    private static final Pattern MD = Pattern.compile("(\\d{1,2})\\s*[/월]\\s*(\\d{1,2})");
    private static final List<String> ACTION_VERBS = List.of(
            "작성", "수정", "개발", "준비", "공유", "검토", "조사", "구현", "배포",
            "전달", "정리", "확인", "제작", "설계", "테스트", "리뷰", "완료");
    private static final List<String> DECISION_MARKERS = List.of(
            "결정", "합의", "하기로", "확정", "승인", "출시", "채택", "진행하기로");

    @Override
    public Briefing analyze(String text) {
        List<String> sentences = new ArrayList<>();
        Matcher sm = SENTENCE.matcher(text == null ? "" : text);
        while (sm.find()) {
            String s = sm.group().trim();
            if (s.length() >= 4) sentences.add(s);
        }

        List<String> decisions = new ArrayList<>();
        List<Briefing.ActionPoint> actionPoints = new ArrayList<>();

        for (String s : sentences) {
            if (contains(s, DECISION_MARKERS) && decisions.size() < 8) {
                decisions.add(s);
            }
            if (contains(s, ACTION_VERBS) && actionPoints.size() < 10) {
                actionPoints.add(new Briefing.ActionPoint(
                        trimTitle(s), extractName(s), extractDate(s), extractPriority(s)));
            }
        }

        String summary = buildSummary(sentences.size(), decisions, actionPoints);
        return new Briefing(summary, decisions, actionPoints);
    }

    private boolean contains(String s, List<String> keywords) {
        return keywords.stream().anyMatch(s::contains);
    }

    private String trimTitle(String s) {
        String t = s.replaceAll("[.!?。]$", "").trim();
        return t.length() > 120 ? t.substring(0, 120) + "…" : t;
    }

    private String extractName(String s) {
        Matcher m = NAME.matcher(s);
        while (m.find()) {
            String name = m.group(1);
            if (!NON_NAMES.contains(name)) return name;
        }
        return null;
    }

    private String extractDate(String s) {
        LocalDate today = LocalDate.now();
        if (s.contains("내일")) return today.plusDays(1).toString();
        if (s.contains("모레")) return today.plusDays(2).toString();
        if (s.contains("다음 주") || s.contains("다음주")) return today.plusWeeks(1).toString();
        if (s.contains("이번 주") || s.contains("이번주") || s.contains("금주"))
            return today.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)).toString();
        for (var e : java.util.Map.of("월요일", DayOfWeek.MONDAY, "화요일", DayOfWeek.TUESDAY,
                "수요일", DayOfWeek.WEDNESDAY, "목요일", DayOfWeek.THURSDAY, "금요일", DayOfWeek.FRIDAY).entrySet()) {
            if (s.contains(e.getKey()))
                return today.with(TemporalAdjusters.next(e.getValue())).toString();
        }
        Matcher m = MD.matcher(s);
        if (m.find()) {
            int mon = Integer.parseInt(m.group(1));
            int day = Integer.parseInt(m.group(2));
            try {
                LocalDate d = LocalDate.of(today.getYear(), mon, day);
                return (d.isBefore(today) ? d.plusYears(1) : d).toString();
            } catch (Exception ignored) {
                return null;
            }
        }
        return null;
    }

    private String extractPriority(String s) {
        if (s.contains("긴급") || s.contains("급하") || s.contains("최우선") || s.contains("ASAP")) return "HIGH";
        if (s.contains("천천히") || s.contains("여유") || s.contains("나중")) return "LOW";
        return "MEDIUM";
    }

    private String buildSummary(int count, List<String> decisions, List<Briefing.ActionPoint> aps) {
        StringBuilder sb = new StringBuilder();
        sb.append("총 ").append(count).append("개 문장을 분석했습니다. ");
        if (!decisions.isEmpty()) {
            sb.append("결정사항 ").append(decisions.size()).append("건");
        }
        if (!aps.isEmpty()) {
            if (!decisions.isEmpty()) sb.append(", ");
            sb.append("Action Point ").append(aps.size()).append("건을 도출했습니다.");
        } else if (decisions.isEmpty()) {
            sb.append("뚜렷한 결정사항이나 실행 항목을 찾지 못했습니다.");
        } else {
            sb.append("을 확인했습니다.");
        }
        return sb.toString();
    }
}
