package com.storycraft.reward.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StreakCheckResponseDto {
    private int currentStreak;
    private boolean streakRewarded;
    private int rewardedPoint;
} 