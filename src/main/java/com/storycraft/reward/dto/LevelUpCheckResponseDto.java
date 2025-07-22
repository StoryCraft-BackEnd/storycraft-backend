package com.storycraft.reward.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LevelUpCheckResponseDto {
    private boolean levelUp;
    private int newLevel;
} 