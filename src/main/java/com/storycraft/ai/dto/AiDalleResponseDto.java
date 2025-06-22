package com.storycraft.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class AiDalleResponseDto {

    @Schema(description = "생성된 이미지 URL", example = "https://cdn/story1.jpg")
    private String imageUrl;
}
