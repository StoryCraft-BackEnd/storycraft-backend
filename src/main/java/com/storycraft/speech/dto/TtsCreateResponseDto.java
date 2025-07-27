package com.storycraft.speech.dto;

import com.storycraft.story.entity.Story;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
@Schema(description = "TTS 음성 생성 응답 DTO")
public class TtsCreateResponseDto {

    @Schema(description = "음성 파일 고유 ID", example = "1")
    private Long ttsId;

    @Schema(description = "TTS대상 동화 ID", example = "1")
    private Long storyId;

    @Schema(description = "TTS대상 단락 ID", example = "1")
    private Long sectionId;

    @Schema(description = "성우 ID", example = "Seoyeon")
    private String voiceId;

    @Schema(description = "TTS 속도", example = "0.8")
    private float speechRate;

    @Schema(description = "언어(eng/kor)", example = "ko")
    private String language;

    @Schema(description = "생성된 TTS(mp3) 파일 URL", example = "https://storycraft.s3.amazonaws.com/tts/story1.mp3")
    private String ttsUrl;

    @Schema(description = "생성 시각", example = "2025-05-15T14:00:00")
    private LocalDateTime createdAt;
}
