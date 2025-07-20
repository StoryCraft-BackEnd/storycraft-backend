package com.storycraft.story.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter

@Schema(description = "동화 수정 요청 DTO")
public class StoryUpdateDto {

    @Schema(description = "수정할 동화 제목", example = "꼬마 용사와 동물 친구들의 모험")
    private String title;

    @Schema(description = "수정할 키워드 목록", example = "[\"고양이\", \"숲\", \"우정\"]")
    private List<String> keywords;
}
