package com.illoon.ai.stt;

import com.illoon.common.exception.ApiException;
import com.illoon.common.exception.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * 데모용 STT. 실제 변환 대신, 업로드된 파일이 텍스트/자막 파일이면 그대로 읽고
 * 아니면 안내 문구를 반환한다.
 * provider=mock 일 때 기본 STT 이자, provider=openai 에서 Whisper 실패 시 폴백.
 */
@Component
public class MockSpeechToText implements SpeechToText {

    @Override
    public String transcribe(MultipartFile audio) {
        if (audio == null || audio.isEmpty()) {
            throw new ApiException(ErrorCode.MEETING_CONTENT_EMPTY);
        }
        String name = audio.getOriginalFilename() == null ? "" : audio.getOriginalFilename().toLowerCase();
        if (name.endsWith(".txt") || name.endsWith(".vtt") || name.endsWith(".srt") || name.endsWith(".md")) {
            try {
                return new String(audio.getBytes(), java.nio.charset.StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new ApiException(ErrorCode.AI_ANALYZE_FAILED);
            }
        }
        return "[데모 모드] 음성 파일 \"" + audio.getOriginalFilename() + "\" 이(가) 업로드되었습니다. "
                + "실제 STT 변환은 AI_PROVIDER=openai + OPENAI_API_KEY 설정 시 동작합니다. "
                + "지금은 회의 내용을 텍스트로 직접 입력해 분석해 주세요.";
    }
}
