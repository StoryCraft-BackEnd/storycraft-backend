package com.storycraft.reward.dto;

import lombok.Data;

@Data
public class BadgeCheckRequestDto {
    private Long childId;
    private String activityType; // STORY_READ, WORD_CLICK, QUIZ_CORRECT, DAILY_MISSION, STREAK, LEVEL_UP 등
} 