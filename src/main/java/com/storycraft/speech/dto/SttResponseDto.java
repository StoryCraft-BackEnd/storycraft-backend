package com.storycraft.speech.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@Builder
@Schema(description = "STT 변환 응답 DTO")
public class SttResponseDto {

    @Schema(description = "키워드 리스트", example = "[\"용사\",\"동물\",\"모험\"]")
    private List<String> keywords;
}
