package com.storycraft.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter @Setter
@Builder
@Schema(description = "퀴즈 응답 DTO")
public class QuizCreateResponseDto {

    @Schema(description = "퀴즈 ID", example = "101")
    private Long quizId;

    @Schema(description = "문제 문장", example = "Which animal went on the Adventure?")
    private String question;

    @Schema(description = "퀴즈 선택지(A~B)")
    private Map<String, String> options;

}
