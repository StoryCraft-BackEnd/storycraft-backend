package com.storycraft.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter

@Schema(description = "이미지 생성 요청 Dto")
public class AiDalleRequestDto {

    @Schema(description = "이미지 생성 프롬프트", example = "꼬마용사 토토와 인사하는 아기 토끼 모모")
    private String prompt;
}
