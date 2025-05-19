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
}
