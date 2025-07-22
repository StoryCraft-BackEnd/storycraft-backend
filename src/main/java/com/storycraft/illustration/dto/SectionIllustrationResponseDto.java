package com.storycraft.illustration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@Builder
@Schema(description = "동화 단락별 삽화 생성 DTO")
public class SectionIllustrationResponseDto {

    @Schema(description = "동화 ID", example = "1")
    private Long storyId;

    @Schema(description = "")
    private List<IllustrationResponseDto> illustrations;
}
