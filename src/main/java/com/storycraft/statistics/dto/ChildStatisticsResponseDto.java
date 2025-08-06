package com.storycraft.statistics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema(description = "자녀별 학습 통계 응답 DTO")
public class ChildStatisticsResponseDto {

    @Schema(description = "생성한 동화 수", example = "15")
    private Long createdStories;

    @Schema(description = "다 읽은 동화 수", example = "12")
    private Long completedStories;

    @Schema(description = "학습한 단어 개수", example = "45")
    private Long learnedWords;

    @Schema(description = "푼 퀴즈 수", example = "120")
    private Long solvedQuizzes;

    @Schema(description = "총 학습 시간 (분)", example = "360")
    private Long totalLearningTimeMinutes;
} 