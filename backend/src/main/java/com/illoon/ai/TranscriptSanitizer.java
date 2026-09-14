package com.illoon.ai;

import java.util.List;

/**
 * Whisper STT가 무음/잡음 구간에서 같은 단어(구절)를 수백 번 반복하는
 * "반복 루프" 현상을 완화한다. 그대로 두면 GPT 분석 토큰을 낭비하고
 * 회의록 원문 가독성도 떨어진다.
 */
final class TranscriptSanitizer {

    /** 이 횟수 이상 연속 반복되면 잘라낸다. */
    private static final int MIN_REPEATS_TO_TRIM = 5;
    /** 잘라낼 때 앞쪽 몇 번은 그대로 남긴다. */
    private static final int KEEP_REPEATS = 2;
    /** 1~3 단어 구절 반복까지 탐지한다. */
    private static final int MAX_NGRAM = 3;

    private TranscriptSanitizer() {}

    static String collapseRepetitions(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }
        List<String> words = List.of(text.trim().split("\\s+"));
        int n = words.size();
        StringBuilder out = new StringBuilder();
        int i = 0;
        while (i < n) {
            int bestLen = 0;
            int bestRepeats = 1;
            for (int len = 1; len <= MAX_NGRAM && i + len <= n; len++) {
                int repeats = countConsecutiveRepeats(words, i, len);
                if (repeats > bestRepeats) {
                    bestRepeats = repeats;
                    bestLen = len;
                }
            }
            if (bestLen > 0 && bestRepeats >= MIN_REPEATS_TO_TRIM) {
                for (int k = 0; k < KEEP_REPEATS; k++) {
                    appendWords(out, words, i + k * bestLen, bestLen);
                }
                out.append(" …(같은 구절 ").append(bestRepeats).append("회 반복 생략)… ");
                i += bestRepeats * bestLen;
            } else {
                out.append(words.get(i)).append(' ');
                i++;
            }
        }
        return out.toString().trim();
    }

    private static int countConsecutiveRepeats(List<String> words, int start, int len) {
        int n = words.size();
        List<String> pattern = words.subList(start, start + len);
        int count = 1;
        int pos = start + len;
        while (pos + len <= n && words.subList(pos, pos + len).equals(pattern)) {
            count++;
            pos += len;
        }
        return count;
    }

    private static void appendWords(StringBuilder out, List<String> words, int start, int len) {
        for (int k = 0; k < len; k++) {
            out.append(words.get(start + k)).append(' ');
        }
    }
}
