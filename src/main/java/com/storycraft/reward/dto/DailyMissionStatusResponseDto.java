package com.storycraft.reward.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DailyMissionStatusResponseDto {
    private Long id;
    private Long childId;
    private String missionCode;
    private Integer progressCount;
    private Boolean completed;
    private LocalDateTime lastUpdatedAt;
} 