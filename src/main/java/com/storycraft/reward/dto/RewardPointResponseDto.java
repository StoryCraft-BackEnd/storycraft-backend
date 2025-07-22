package com.storycraft.reward.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RewardPointResponseDto {
    private Long id;
    private Long childId;
    private String rewardType;
    private String context;
    private Integer points;
    private LocalDateTime awardedAt;
} 