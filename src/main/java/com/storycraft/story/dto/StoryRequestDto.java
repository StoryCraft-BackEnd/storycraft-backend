package com.storycraft.story.dto;

import com.storycraft.profile.entity.ChildProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter

@Schema(description = "동화 생성 요청 DTO")
public class StoryRequestDto {

    @Schema(description = "동화 생성 키워드", example = "[\"용사\",\"동물\",\"모험\"]")
    private List<String> keywords;

    @Schema(description = "자녀 ID", example = "1")
    private Long childId;
}
