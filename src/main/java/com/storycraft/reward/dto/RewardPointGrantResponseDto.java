package com.storycraft.reward.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class RewardPointGrantResponseDto {
    private Integer rewardedPoint;
    private Integer totalPoint;
    private LevelUpCheckResponseDto levelUp; // 레벨업 정보
    private List<BadgeCheckResponseDto.NewBadgeDto> newBadges; // 새로 획득한 배지들
} 