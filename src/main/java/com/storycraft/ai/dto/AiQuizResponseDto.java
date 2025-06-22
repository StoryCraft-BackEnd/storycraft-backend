package com.storycraft.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
@Getter
@Setter
@Schema(description = "GPT 퀴즈 생성 응답 Dto")
public class AiQuizResponseDto {

    @Schema(description = "문제 문장", example = "Which animal went on the Adventure?")
    private String question;

    @Schema(
            description = "퀴즈 선택지(A~B)",
            example = "{\"A\": \"Lion\", \"B\": \"Tiger\", \"C\": \"Rabbit\", \"D\": \"Fox\"}"
    )
    private Map<String, String> options;

    @Schema(description = "퀴즈 정답", example = "C")
    private String answer;
}
