package com.storycraft.ai.dto;

import com.storycraft.profile.entity.ChildProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter

@Schema(description = "동화 생성 요청 Dto")
public class AiStoryRequestDto {

    @Schema(description = "동화 생성 키워드", example = "꼬마 용사와 동물 친구들의 숲속 모험 이야기")
    private String keyword;

    @Schema(description = "자녀 ID", example = "child-uuid-1234")
    private Long childId;
}
