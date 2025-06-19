package com.storycraft.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AiWhisperResponseDto {

    @Schema(description = "음성에서 변환된 텍스트", example = "꼬마 용사, 동물친구들, 모헙, 숲")
    private String text;
}
