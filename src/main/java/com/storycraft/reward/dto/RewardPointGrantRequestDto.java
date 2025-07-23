package com.storycraft.reward.dto;

import lombok.Data;

@Data
public class RewardPointGrantRequestDto {
    private String rewardType;
    private String context;
    private Long childId; // 명시적으로 지정할 수 있도록(필요시)
} 