package com.storycraft.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter @Setter
@Schema(description = "퀴즈 생성 요청 Dto")
public class QuizCreateRequestDto {

    @Schema(description = "문제 문장", example = "Which animal went on the Adventure?")
    private String question;

    @Schema(description = "객관식 보기", example = "{\"A\": \"Lion\", \"B\": \"Tiger\", \"C\": \"Rabbit\", \"D\": \"Fox\"}")
    private Map<String, String> options;

    @Schema(description = "정답", example = "C")
    private String answer;
}
