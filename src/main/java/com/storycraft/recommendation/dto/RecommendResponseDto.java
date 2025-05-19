package com.storycraft.recommendation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Schema(description = "동화 추천 응답 DTO")
@Builder
public class RecommendResponseDto {

    @Schema(description = "추천 동화 ID", example = "1")
    private Long storyId;

    @Schema(description = "추천 동화 제목", example = "꼬마 용사와 동물 친구들의 모험")
    private String title;

    @Schema(description = "추천 동화 요약", example = "숲을 꼬마 용사와 동물 친구들의 이야기")
    private String summary;

    @Schema(description = "추천 동화 썸네일 URL", example = "https://cdn/story1.jpg")
    private String thumbnailUrl;

}
