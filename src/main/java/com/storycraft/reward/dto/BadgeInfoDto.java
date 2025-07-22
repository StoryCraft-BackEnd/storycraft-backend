package com.storycraft.reward.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BadgeInfoDto {
    private String badgeCode;
    private String badgeName;
    private String condition;
    private String category; // BASIC_LEARNING, MILESTONE, STREAK, SPECIAL_CHALLENGE
} 