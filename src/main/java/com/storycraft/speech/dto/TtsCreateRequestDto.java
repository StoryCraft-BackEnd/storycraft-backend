package com.storycraft.speech.dto;

import com.storycraft.story.entity.Story;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
@Schema(description = "TTS 음성 생성 요청 DTO")
public class TtsCreateRequestDto {

    @Schema(description = "읽을 동화 ID", example = "1")
    private Long storyId;

    @Schema(description = "읽을 단락 ID", example = "1")
    private Long sectionId;

    @Schema(description = "성우", example = "Seoyeon")
    private String voiceId;

    @Schema(description = "TTS 속도", example = "0.8")
    private float speechRate;
}
