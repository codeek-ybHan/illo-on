package com.illoon.ai.stt;

import org.springframework.web.multipart.MultipartFile;

/**
 * 음성 파일 → 텍스트. (기획서 §8-3)
 */
public interface SpeechToText {
    String transcribe(MultipartFile audio);
}
