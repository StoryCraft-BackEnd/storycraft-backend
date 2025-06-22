package com.storycraft.ai.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "GPT 퀴즈 생성 요청 Dto")
public class AiQuizRequestDto {

    @Schema(description = "생성할 퀴즈의 기반이 될 동화 ID", example = "1")
    private Long storyId;

    @Schema(description = "사용자가 하이라이트 표시한 단어", example = "[\"adventure\", \"dragon\"]")
    private List<String> highlightedWords;

}
