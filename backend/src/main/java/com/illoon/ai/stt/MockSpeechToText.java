package com.illoon.ai.stt;

import com.illoon.common.exception.ApiException;
import com.illoon.common.exception.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * 실제 음성 변환 없이, 업로드된 파일이 텍스트/자막(.txt·.vtt·.srt·.md)이면 그대로 읽는다.
 * provider=mock 의 기본 STT 이자, provider=openai 에서 Whisper 가 실패했을 때의 폴백.
 *
 * <p>음성 파일은 변환하지 못한다 → 안내 문구를 "변환 결과"인 척 돌려주면
 * LLM 이 그 문구를 회의록으로 착각해 없는 회의를 지어낸다(담당자·기한 헛것). 그래서 명시적으로 실패시킨다.
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
                throw new ApiException(ErrorCode.STT_FAILED);
            }
        }
        throw new ApiException(ErrorCode.STT_FAILED,
                "음성 파일 변환은 OpenAI 설정이 필요합니다. 자막 파일(.txt·.vtt·.srt)을 올리거나 회의 내용을 직접 입력해 주세요.");
    }
}
