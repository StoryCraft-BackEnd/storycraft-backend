package com.storycraft.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
@Schema(description = "퀴즈 결과 DTO")
public class QuizResultDto {

    @Schema(description = "퀴즈 ID", example = "101")
    private Long quizId;

    @Schema(description = "문제 문장", example = "Which animal went on the Adventure?")
    private String question;

    @Schema(description = "사용자가 선택한 답", example = "C")
    private String selectedAnswer;

    @Schema(description = "문제 정답", example = "C")
    private String correctAnswer;

    @Schema(description = "정답 여부", example = "true")
    private boolean isCorrect;

}
