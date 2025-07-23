package com.storycraft.reward.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RewardHistoryItemDto {
    private String date; // yyyy-MM-dd
    private String type; // POINT or BADGE
    // POINT용
    private String rewardType;
    private String context;
    private Integer value;
    // BADGE용
    private String badgeCode;
    private String badgeName;
} 