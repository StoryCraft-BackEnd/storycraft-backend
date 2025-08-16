package com.storycraft.story.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "동화 진행 상황 요청 DTO")
public class StoryProgressRequestDto {

    @Schema(description = "자녀 프로필 ID", example = "1", required = true)
    private Long childId;

    @Schema(description = "동화 ID", example = "1", required = true)
    private Long storyId;

    @Schema(description = "학습 시간 (초)", example = "300", required = true)
    private Integer learnedTimeInSecond;
}
