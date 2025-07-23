package com.storycraft.reward.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RewardBadgeResponseDto {
    private Long id;
    private Long childId;
    private String badgeCode;
    private String badgeName;
    private LocalDateTime awardedAt;
} 