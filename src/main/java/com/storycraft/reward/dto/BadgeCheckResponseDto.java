package com.storycraft.reward.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class BadgeCheckResponseDto {
    private List<NewBadgeDto> newBadges;
    
    @Data
    @AllArgsConstructor
    public static class NewBadgeDto {
        private String badgeCode;
        private String badgeName;
    }
} 