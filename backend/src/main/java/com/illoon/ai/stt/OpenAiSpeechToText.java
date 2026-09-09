package com.illoon.ai.stt;

import com.illoon.common.exception.ApiException;
import com.illoon.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * OpenAI Whisper STT. app.ai.provider=openai 필요.
 * 변환 실패(쿼터 초과·장애 등) 시 {@link MockSpeechToText} 로 degrade — 502 대신 안내 문구.
 */
@Slf4j
@Component
@Primary
@ConditionalOnProperty(name = "app.ai.provider", havingValue = "openai")
public class OpenAiSpeechToText implements SpeechToText {

    private final OpenAiAudioTranscriptionModel model;
    private final MockSpeechToText fallback;

    public OpenAiSpeechToText(OpenAiAudioTranscriptionModel model, MockSpeechToText fallback) {
        this.model = model;
        this.fallback = fallback;
    }

    @Override
    public String transcribe(MultipartFile audio) {
        if (audio == null || audio.isEmpty()) {
            throw new ApiException(ErrorCode.MEETING_CONTENT_EMPTY);
        }
        try {
            var resource = new ByteArrayResource(audio.getBytes()) {
                @Override
                public String getFilename() {
                    return audio.getOriginalFilename();
                }
            };
            return model.call(new AudioTranscriptionPrompt(resource)).getResult().getOutput();
        } catch (Exception e) {
            log.warn("Whisper STT failed, falling back to mock: {}", e.getMessage());
            return fallback.transcribe(audio);
        }
    }
}
