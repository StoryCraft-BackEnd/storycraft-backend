package com.storycraft.dictionary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Schema(description = "단어 저장 요청 DTO")
public class SaveWordRequestDto {

    @Schema(description = "자녀 프로필 ID", example = "123")
    private String childId;

    @Schema(description = "하이라이트 표시한 단어", example = "adventure")
    private String word;
}
