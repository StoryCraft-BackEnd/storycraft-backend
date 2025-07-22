package com.storycraft.illustration.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter @Setter
@Schema(description = "삽화 생성 요청 DTO")
public class IllustrationRequestDto {

    @Schema(description = "대상 동화 ID", example = "1")
    private Long storyId;

    @Schema(description = "대상 단락 ID", example = "1")
    private Long sectionId;
}
