package com.storycraft.reward.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RewardPointGrantResponseDto {
    private Integer rewardedPoint;
    private Integer totalPoint;
} 