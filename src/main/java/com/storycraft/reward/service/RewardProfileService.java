package com.storycraft.reward.service;

import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.profile.repository.ChildProfileRepository;
import com.storycraft.reward.dto.RewardProfileResponseDto;
import com.storycraft.reward.entity.RewardBadge;
import com.storycraft.reward.entity.StreakStatus;
import com.storycraft.reward.repository.RewardPointRepository;
import com.storycraft.reward.repository.StreakStatusRepository;
import com.storycraft.reward.dto.DailyMissionStatusDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RewardProfileService {
    private final ChildProfileRepository childProfileRepository;
    private final RewardPointRepository rewardPointRepository;
    private final RewardBadgeService rewardBadgeService;
    private final StreakStatusRepository streakStatusRepository;
    private final DailyMissionService dailyMissionService;

    @Transactional(readOnly = true)
    public RewardProfileResponseDto getRewardProfile(Long childId) {
        ChildProfile child = childProfileRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자녀입니다."));

        // 1. 포인트 잔액
        int totalPoints = rewardPointRepository.sumPointsByChild(child);

        // 2. 현재 레벨 (500점마다 1레벨)
        int currentLevel = (totalPoints / 500) + 1;

        // 3. 보유 배지 목록
        List<RewardBadge> badges = rewardBadgeService.getChildBadges(child);
        List<RewardProfileResponseDto.BadgeDto> badgeDtos = badges.stream()
                .map(badge -> new RewardProfileResponseDto.BadgeDto(
                        badge.getId(),
                        badge.getBadgeCode(),
                        badge.getBadgeName(),
                        badge.getCreatedAt()
                ))
                .collect(Collectors.toList());

        // 4. 연속 학습 일수
        int streakDays = 0;
        StreakStatus streakStatus = streakStatusRepository.findByChild(child).orElse(null);
        if (streakStatus != null) {
            streakDays = streakStatus.getCurrentStreak();
        }

        // 5. 데일리 미션 상태
        String dailyMissionStatus = getDailyMissionStatus(childId);

        return new RewardProfileResponseDto(
                totalPoints,
                currentLevel,
                badgeDtos,
                streakDays,
                dailyMissionStatus
        );
    }

    private String getDailyMissionStatus(Long childId) {
        try {
            List<DailyMissionStatusDto> missionStatuses = dailyMissionService.getDailyMissionStatus(childId);
            boolean allCompleted = missionStatuses.stream().allMatch(DailyMissionStatusDto::isCompleted);
            boolean anyInProgress = missionStatuses.stream().anyMatch(status -> status.getProgressCount() > 0);
            
            if (allCompleted) {
                return "completed";
            } else if (anyInProgress) {
                return "in_progress";
            } else {
                return "not_started";
            }
        } catch (Exception e) {
            return "not_started";
        }
    }
} 