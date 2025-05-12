package com.storycraft.story.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter

@Schema(description = "동화 생성 요청 DTO")
public class StoryRequestDto {

    @Schema(description = "AI 프롬프트 키워드", example = "꼬마 용사와 동물 친구들의 모험")
    private String prompt;

    @Schema(description = "자녀 ID", example = " child-uuid-1234")
    private String childId;
}
