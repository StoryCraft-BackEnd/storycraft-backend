package com.storycraft.reward.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DailyMissionCheckResponseDto {
    private int rewardedPoint;
    private boolean alreadyClaimed;
} 