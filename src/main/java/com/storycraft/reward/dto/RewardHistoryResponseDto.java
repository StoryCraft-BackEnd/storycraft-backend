package com.storycraft.reward.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RewardHistoryResponseDto {
    private Long id;
    private Long childId;
    private String type; // point or badge
    private String codeOrContext; // badgeCode or context
    private String name; // badgeName or null
    private Integer points; // point 지급시만 값
    private LocalDateTime awardedAt;
} 