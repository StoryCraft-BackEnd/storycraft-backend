package com.storycraft.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter @Setter

@Schema(description = "퀴즈 제출 요청 DTO")
public class QuizSubmitRequestDto {

    @Schema(description = "퀴즈를 푼 자녀 ID", example = "123")
    private String childId;

    @Schema(description = "사용자가 선택한 보기", example = "C")
    private String selectedAnswer;
}

