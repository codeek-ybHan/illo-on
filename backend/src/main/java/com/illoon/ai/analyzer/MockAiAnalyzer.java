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
import java.util.stream.Collectors;

/**
 * OpenAI 키 없이 데모/개발용. 규칙 기반으로 결정사항·Action Point 를 추출하고
 * Action Point 업무명을 간결하게 정리한다.
 * (app.ai.provider=mock, 기본값 — 실제 품질은 OpenAiAnalyzer)
 */
@Component
@ConditionalOnProperty(name = "app.ai.provider", havingValue = "mock", matchIfMissing = true)
public class MockAiAnalyzer implements AiAnalyzer {

    private static final Pattern SENTENCE = Pattern.compile("[^.!?\\n。]+[.!?。]?");
    private static final Pattern PREFIX = Pattern.compile(
            "^(?:그리고|또한|그래서|따라서|그러면|이에|이번에는?|한편|아울러|다음으로)\\s+");

    /** 한국어 성씨 집합 — 이름 오탐 방지 */
    private static final java.util.Set<Character> SURNAMES = new java.util.HashSet<>();
    static {
        for (char c : ("김이박최정강조윤장임한오서신권황안송전홍유고문양손배백허남심노하곽성차주우구민류"
                + "라전방석길위표명기반왕금옥육인맹제탁국어은편용예봉경").toCharArray()) SURNAMES.add(c);
    }

    /** "홍길동가/이/께서" — 사람 담당자 지목 */
    private static final Pattern ASSIGNEE = Pattern.compile(
            "([가-힣]{2,4})(?:님|씨|대리|과장|차장|부장|팀장|매니저|리드)?\\s?(?:가|이|께서)(?![가-힣])");

    private static final Pattern DATE_PHRASE = Pattern.compile(
            "\\d{1,2}\\s*[/월]\\s*\\d{1,2}\\s*일?\\s?(?:까지|전까지|안에|내)?"
                    + "|(?:이번|다음|차)\\s?주\\s?(?:초|말)?\\s?(?:월요일|화요일|수요일|목요일|금요일|토요일|일요일)?\\s?(?:까지)?"
                    + "|(?:이번|다음)\\s?달\\s?(?:초|말)?\\s?(?:까지)?"
                    + "|(?:내일|모레|글피|오늘)\\s?(?:까지)?"
                    + "|(?:월요일|화요일|수요일|목요일|금요일|토요일|일요일)\\s?까지");

    private static final Pattern ADVERB = Pattern.compile(
            "긴급(?:하게|히)?|급하게|서둘러|되도록\\s?빨리|최우선으로|우선|일단|먼저|가급적|되도록|천천히|여유\\s?있게|나중에|가능하면");

    private static final String VERBS =
            "작성|작업|수정|보완|개발|구현|설계|기획|준비|공유|전달|검토|리뷰|확인|점검|조사|분석|정리|취합|반영|"
                    + "배포|릴리스|테스트|제작|디자인|협의|조율|정의|추가|삭제|변경|보고|발표|산정|검증|세팅|구축|이관|"
                    + "업데이트|패치|최적화|리팩터링|문서화|수립|도출|합의";
    private static final Pattern VERB_TAIL = Pattern.compile(
            "(" + VERBS + ")\\s?(?:하기로|하기|한다|합니다|해야|하면|해\\s?주|했|할|해|함|하는|하고|하자|하죠|해요|예정)");
    private static final Pattern ACTION_TAIL = Pattern.compile("해야|하기로|하면 된|할 예정|필요");

    /** 업무명 꼬리에서 걷어낼 서술 어미 */
    private static final Pattern ENDING = Pattern.compile(
            "\\s?(?:하기로\\s?(?:했습니다|함|했음|했다|결정했습니다)?|해야\\s?(?:합니다|한다|함)?|하면\\s?(?:됩니다|된다)?"
                    + "|할\\s?예정(?:입니다|이다|임)?|예정입니다|합니다|한다|했습니다|해요|하죠|필요합니다|필요함|이\\s?필요합니다)"
                    + "\\s*[.!?。]?\\s*$");

    private static final Pattern DECISION = Pattern.compile(
            "결정|합의(?!점)|확정|승인|채택|하기로\\s?했|하기로\\s?함|출시하기로|런칭하기로|오픈하기로|보류하기로|중단하기로|취소하기로");
    private static final Pattern INTRO = Pattern.compile(
            "회의(?:는|입니다|이다|를\\s?진행|의\\s?목적)|위한\\s?(?:것|자리)|목적은|안건은|논의하(?:기|고자)");

    @Override
    public Briefing analyze(String text) {
        List<String> sentences = new ArrayList<>();
        Matcher sm = SENTENCE.matcher(text == null ? "" : text);
        while (sm.find()) {
            String s = PREFIX.matcher(sm.group().trim()).replaceFirst("");
            if (s.length() >= 4) sentences.add(s);
        }

        List<String> decisions = new ArrayList<>();
        List<Briefing.ActionPoint> actionPoints = new ArrayList<>();
        java.util.Set<Integer> consumed = new java.util.HashSet<>();

        for (int i = 0; i < sentences.size(); i++) {
            String s = sentences.get(i);
            // 도입/목적 문장만 건너뜀 (내용이 여러 문장일 때 첫 문장이 안건이면 유지)
            if (INTRO.matcher(s).find()
                    && !VERB_TAIL.matcher(s).find() && !DECISION.matcher(s).find()) {
                consumed.add(i);
                continue;
            }
            String assignee = extractName(s);
            boolean hasVerb = VERB_TAIL.matcher(s).find();

            if (assignee != null && hasVerb && actionPoints.size() < 15) {
                actionPoints.add(ap(s, assignee));
                consumed.add(i);
            } else if (DECISION.matcher(s).find() && decisions.size() < 12) {
                decisions.add(cleanDecision(s));
                consumed.add(i);
            } else if (hasVerb && ACTION_TAIL.matcher(s).find() && actionPoints.size() < 15) {
                actionPoints.add(ap(s, assignee));
                consumed.add(i);
            }
        }

        List<String> highlights = buildHighlights(sentences, consumed);
        String overview = buildOverview(sentences, decisions, actionPoints);
        return new Briefing(overview, highlights, decisions, actionPoints);
    }

    private List<String> buildHighlights(List<String> sentences, java.util.Set<Integer> consumed) {
        // 논의·배경 문장 (결정사항·Action Point 로 이미 뽑힌 문장은 제외).
        // 규칙 기반이라 원문 문장을 다듬어 그대로 제시한다 — 정교한 요약은 OpenAI provider 담당.
        List<String> out = new ArrayList<>();
        for (int i = 0; i < sentences.size() && out.size() < 7; i++) {
            if (consumed.contains(i)) continue;
            String s = sentences.get(i).replaceAll("[.!?。]$", "").trim();
            if (s.length() >= 6) out.add(s);
        }
        return out;
    }

    private String buildOverview(List<String> sentences, List<String> decisions,
                                 List<Briefing.ActionPoint> aps) {
        for (String s : sentences) {
            if (INTRO.matcher(s).find()) {
                return s.replaceAll("[.!?。]$", "").replaceAll("^이번\\s?회의는\\s?", "")
                        .replaceAll("\\s?(?:을|를)?\\s?위한\\s?것?(?:입니다)?$", " 논의").trim();
            }
        }
        if (!decisions.isEmpty()) {
            String d = clip(decisions.get(0).replaceAll("^논의 결과 ", ""), 50);
            return decisions.size() > 1 ? d + " 등 " + decisions.size() + "건 결정" : d;
        }
        if (!aps.isEmpty()) {
            return aps.stream().limit(3).map(Briefing.ActionPoint::title)
                    .collect(Collectors.joining(", ")) + " 등 후속 업무 도출";
        }
        return "회의 내용 분석 완료";
    }

    private String clip(String s, int n) {
        return s.length() > n ? s.substring(0, n) + "…" : s;
    }

    private Briefing.ActionPoint ap(String s, String assignee) {
        return new Briefing.ActionPoint(summarizeTitle(s, assignee), assignee, extractDate(s), extractPriority(s));
    }

    // ---------- Action Point 업무명 ----------

    private String summarizeTitle(String sentence, String assignee) {
        String s = sentence;
        // 알고 있는 담당자 이름만 정확히 제거 (오탐 방지)
        if (assignee != null) {
            s = s.replaceAll(Pattern.quote(assignee)
                    + "(?:님|씨|대리|과장|차장|부장|팀장|매니저|리드)?\\s?(?:가|이|께서|은|는)?\\s?", " ");
        }
        s = DATE_PHRASE.matcher(s).replaceAll(" ");
        s = ADVERB.matcher(s).replaceAll(" ");
        s = ENDING.matcher(s).replaceAll("");
        s = s.replaceAll("[.!?。]", " ").replaceAll("\\s+", " ").trim();
        // 남은 "하기로 / 해야 / 하면" 등 잘린 꼬리
        s = s.replaceAll("(?:하기로|해야|하면\\s?되|할\\s?예정|이\\s?필요|가\\s?필요)\\s*$", "").trim();
        s = s.replaceAll("[,·]+$", "").trim();
        // "명세는 작성" -> "명세 작성" (동사 바로 앞 토큰의 주격/목적격 조사만)
        Matcher jm = Pattern.compile("([가-힣]+)(?:는|은|를|을)\\s+(" + VERBS + ")$").matcher(s);
        if (jm.find()) s = s.substring(0, jm.start()) + jm.group(1) + " " + jm.group(2);

        if (s.length() < 2) s = ENDING.matcher(sentence).replaceAll("").replaceAll("[.!?。]$", "").trim();
        return s.length() > 60 ? s.substring(0, 60) + "…" : s;
    }

    private String cleanDecision(String s) {
        String d = ASSIGNEE.matcher(s).replaceAll("");
        return d.replaceAll("\\s+", " ").trim().replaceAll("[.]$", "");
    }

    // ---------- 담당자 / 기한 / 우선순위 ----------

    private String extractName(String s) {
        Matcher m = ASSIGNEE.matcher(s);
        while (m.find()) {
            String name = m.group(1);
            if (name.length() >= 2 && SURNAMES.contains(name.charAt(0))) return name;
        }
        return null;
    }

    private String extractDate(String s) {
        LocalDate today = LocalDate.now();
        if (s.contains("내일")) return today.plusDays(1).toString();
        if (s.contains("모레")) return today.plusDays(2).toString();
        if (s.contains("다음 주") || s.contains("다음주") || s.contains("차주")) return today.plusWeeks(1).toString();
        if (s.contains("이번 주") || s.contains("이번주") || s.contains("금주"))
            return today.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)).toString();
        for (var e : java.util.Map.of("월요일", DayOfWeek.MONDAY, "화요일", DayOfWeek.TUESDAY,
                "수요일", DayOfWeek.WEDNESDAY, "목요일", DayOfWeek.THURSDAY, "금요일", DayOfWeek.FRIDAY).entrySet()) {
            if (s.contains(e.getKey())) return today.with(TemporalAdjusters.next(e.getValue())).toString();
        }
        Matcher m = Pattern.compile("(\\d{1,2})\\s*[/월]\\s*(\\d{1,2})\\s*일?").matcher(s);
        if (m.find()) {
            try {
                LocalDate d = LocalDate.of(today.getYear(),
                        Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
                return (d.isBefore(today) ? d.plusYears(1) : d).toString();
            } catch (Exception ignored) {
                return null;
            }
        }
        return null;
    }

    private String extractPriority(String s) {
        if (s.contains("긴급") || s.contains("급하") || s.contains("최우선") || s.contains("ASAP")
                || s.contains("서둘러") || s.contains("되도록 빨리")) return "HIGH";
        if (s.contains("천천히") || s.contains("여유") || s.contains("나중")) return "LOW";
        return "MEDIUM";
    }
}
