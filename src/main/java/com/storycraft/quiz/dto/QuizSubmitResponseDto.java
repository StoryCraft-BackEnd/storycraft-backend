package com.storycraft.quiz.dto;

import com.storycraft.profile.entity.ChildProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@Builder
@Schema(description = "퀴즈 제출 응답 DTO")
public class QuizSubmitResponseDto {

    @Schema(description = "동화 ID", example = "1")
    private Long storyId;

    @Schema(description = "자녀 ID", example = "123")
    private Long childId;

    @Schema(description = "총 문제 수", example = "10")
    private int total;

    @Schema(description = "맞힌 개수", example = "8")
    private int correct;

    @Schema(description = "제출 시각", example = "2025-05-15T14:00:00")
    private String submittedAt;

    @Schema(description = "문항별 결과 리스트")
    private List<QuizResultDto> results;
}
