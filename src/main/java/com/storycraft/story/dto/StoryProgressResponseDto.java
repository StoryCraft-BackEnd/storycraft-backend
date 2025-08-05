package com.storycraft.story.dto;

import com.storycraft.story.entity.StoryProgress;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "학습 상태 정보 응답 DTO")
public class StoryProgressResponseDto {

    @Schema(description = "동화 ID", example = "1")
    private Long storyId;

    @Schema(description = "읽음 여부", example = "true")
    private boolean isRead;

    @Schema(description = "학습 시간(분)", example = "3")
    private int learnedMinutes;

    @Schema(description = "학습 시간(초)", example = "35")
    private int learnedSeconds;

    @Schema(description = "읽은 시각", example = "2025-01-01T15:00:00")
    private LocalDateTime readAt;

    public static StoryProgressResponseDto fromEntity(StoryProgress progress) {
        return StoryProgressResponseDto.builder()
                .storyId(progress.getStory().getId())
                .isRead(progress.isRead())
                .learnedMinutes(progress.getLearnedMinutes())
                .learnedSeconds(progress.getLearnedSeconds())
                .readAt(progress.getReadAt())
                .build();
    }
}
