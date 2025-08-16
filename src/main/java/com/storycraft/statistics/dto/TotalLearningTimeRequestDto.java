package com.storycraft.statistics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "총 학습 시간 저장 요청 DTO")
public class TotalLearningTimeRequestDto {

    @Schema(description = "자녀 프로필 ID", example = "1", required = true)
    private Long childId;

    @Schema(description = "총 학습 시간 (분)", example = "120", required = true)
    private Long totalLearningTimeMinutes;

    @Schema(description = "학습 시간 업데이트 날짜 (yyyy-MM-dd HH:mm:ss)", example = "2024-01-15 14:30:00")
    private String updatedAt;
}
