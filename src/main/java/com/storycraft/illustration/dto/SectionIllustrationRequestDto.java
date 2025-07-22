package com.storycraft.illustration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter

@Schema(description = "동화 단락 기반 삽화 생성 요청 DTO")
public class SectionIllustrationRequestDto {

    @Schema(description = "대상 동화 ID", example = "1")
    private Long storyId;
}