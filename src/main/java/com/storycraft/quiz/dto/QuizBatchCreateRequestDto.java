package com.storycraft.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "퀴즈 여러 개 생성 요청 DTO")
public class QuizBatchCreateRequestDto {

    @Schema(description = "동화 ID", example = "1")
    private Long storyId;

    @Schema(description = "생성할 퀴즈 리스트")
    private List<QuizCreateRequestDto> quizList;
}