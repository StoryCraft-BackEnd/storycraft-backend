package com.storycraft.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
@Schema(description = "퀴즈 제출 응답 DTO")
public class QuizSubmitResponseDto {

    @Schema(description = "퀴즈 ID", example = "101")
    private Long quizId;

    @Schema(description = "퀴즈를 푼 자녀 ID", example = "123")
    private ChildProfile childId;

    @Schema(description = "정답 여부", example = "true")
    private boolean isCorrect;

    @Schema(description = "정답", example = "C")
    private String correctAnswer;

    @Schema(description = "제출 시각", example = "2025-05-15T14:00:00")
    private String submittedAt;
}
