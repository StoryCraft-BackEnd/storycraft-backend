package com.storycraft.recommendation.dto;

import com.storycraft.profile.entity.ChildProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Schema(description = " 추천 피드백 요청 DTO")
public class RecommendationFeedbackRequestDto {

    @Schema(description = "자녀 프로필 ID", example = "123")
    private ChildProfile childId;

    @Schema(description = "동화 ID", example = "1")
    private Long storyId;

    @Schema(description = "사용자의 추천 여부", example = "true")
    private Boolean liked;

    @Schema(description = "사용자의 동화 읽음 여부", example = "true")
    private Boolean read;
}
