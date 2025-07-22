package com.storycraft.reward.dto;

import lombok.Data;

@Data
public class RewardPointRequestDto {
    private Long childId;
    private String rewardType;
    private String context;
    private Integer points;
} 