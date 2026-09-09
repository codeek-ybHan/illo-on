package com.illoon.ai.stt;

import com.illoon.common.exception.ApiException;
import com.illoon.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * OpenAI Whisper STT. app.ai.provider=openai 필요.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "app.ai.provider", havingValue = "openai")
public class OpenAiSpeechToText implements SpeechToText {

    private final OpenAiAudioTranscriptionModel model;

    public OpenAiSpeechToText(OpenAiAudioTranscriptionModel model) {
        this.model = model;
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
            log.error("Whisper STT failed", e);
            throw new ApiException(ErrorCode.AI_ANALYZE_FAILED);
        }
    }
}
