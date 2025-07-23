package com.storycraft.reward.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class RewardProfileResponseDto {
    private int points;
    private int level;
    private List<BadgeDto> badges;
    private int streakDays;
    private String dailyMissionStatus; // "completed", "in_progress", "not_started"
    
    @Data
    @AllArgsConstructor
    public static class BadgeDto {
        private Long id;
        private String badgeCode;
        private String badgeName;
        private LocalDateTime awardedAt;
    }
} 