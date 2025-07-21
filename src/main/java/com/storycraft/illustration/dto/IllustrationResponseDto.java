package com.storycraft.illustration.dto;


import com.storycraft.illustration.entity.Illustration;
import com.storycraft.story.entity.StorySection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter @Setter
@Builder
@Schema(description = "삽화 응답 DTO")
public class IllustrationResponseDto {

    @Schema(description = "삽화 ID", example = "1")
    private Long illustrationId;

    @Schema(description = "동화 ID", example = "1")
    private Long storyId;

    @Schema(description = "단락 순서", example = "2")
    private int orderIndex;

    @Schema(description = "이미지 URL", example = "https://cdn/story1.jpg")
    private String imageUrl;

    @Schema(description = "삽화 설명", example = "숲속에서 모험중인 꼬마용사와 강아지 친구")
    private String description;

    @Schema(description = "생성 일시", example = "2025-05-12T22:15:30")
    private String createdAt;


    public static IllustrationResponseDto from(Illustration illustration, StorySection section) {
        return IllustrationResponseDto.builder()
                .illustrationId(illustration.getIllustrationId())
                .orderIndex(section.getOrderIndex())
                .imageUrl(illustration.getImageUrl())
                .description(illustration.getDescription())
                .createdAt(illustration.getCreatedAt().toString())
                .build();
    }

}
