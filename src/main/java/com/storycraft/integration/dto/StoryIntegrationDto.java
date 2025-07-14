package com.storycraft.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "동화 조회 통합 API")
public class StoryIntegrationDto {

    @Schema(description = "동화 ID", example = "1")
    private Long storyId;

    @Schema(description = "동화 제목", example = "꼬마 용사와 동물 친구들의 모험")
    private String title;

    @Schema(description = "동화 내용", example = "Once upon a time...")
    private String content;

    @Schema(description = "이미지 URL", example = "https://storycraft.s3.amazonaws.com/image/story1.jpg")
    private String imageUrl;

    @Schema(description = "생성된 TTS(mp3) 파일 URL", example = "https://storycraft.s3.amazonaws.com/tts/story1.mp3")
    private String ttsUrl;

    @Schema(description = "생성 시각", example = "2025-05-15T14:00:00")
    private LocalDateTime createdAt;
}
