package com.storycraft.dictionary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
@Schema(description = "단어 조회 응답 DTO")
public class WordResponseDto {

    @Schema(description = "단어 ID", example = "1")
    private Long wordId;

    @Schema(description = "하이하이트 표시한 단어", example = "adventure")
    private String word;

    @Schema(description = "뜻", example = "모험")
    private String meaning;

    @Schema(description = "예문", example = "He went on an adventure through the forest.")
    private String example;

    @Schema(description = "저장 시각", example = "2025-05-12T22:15:30")
    private LocalDateTime savedAt;
}
