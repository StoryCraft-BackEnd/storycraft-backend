package com.storycraft.story.dto;

import com.storycraft.story.entity.StorySection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "동화 단락 응답 DTO")
public class StorySectionDto {

    @Schema(description = "단락 ID", example = "1")
    private int sectionId;

    @Schema(description = "단락 순서", example = "2")
    private int orderIndex;

    @Schema(description = "단락 텍스트 내용", example = "Once upon a time, a little fox lived in the forest.")
    private String paragraphText;

    @Schema(description = "해당 단락이 속한 동화 ID", example = "1")
    private Long storyId;

    public static StorySectionDto fromEntity(StorySection section) {
        return StorySectionDto.builder()
                .sectionId(section.getSectionId())
                .orderIndex(section.getOrderIndex())
                .paragraphText(section.getParagraphText())
                .storyId(section.getStory().getId())
                .build();
    }
}