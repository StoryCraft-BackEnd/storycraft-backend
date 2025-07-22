package com.storycraft.reward.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DailyMissionStatusDto {
    private String missionCode;
    private String description;
    private int progressCount;
    private boolean completed;
} 