package com.storycraft.story.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
@Schema(description = "동화 응답 DTO")
public class StoryResponseDto {

    @Schema(description = "동화 ID", example = "1")
    private Long storyId;

    @Schema(description = "동화 제목", example = "꼬마 용사와 동물 친구들의 모험")
    private String title;

    @Schema(description = "동화 내용", example = "옛날 옛적에...")
    private String content;

    @Schema(description = "생성일시", example = "2025-01-01T15:00:00")
    private String createdAt;

    @Schema(description = "수정 일시", example = "2025-01-01T16:00:00", nullable = true)
    private String updatedAt;
}
