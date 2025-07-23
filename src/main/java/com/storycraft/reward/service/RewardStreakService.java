package com.storycraft.reward.service;

import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.profile.repository.ChildProfileRepository;
import com.storycraft.reward.dto.StreakCheckRequestDto;
import com.storycraft.reward.dto.StreakCheckResponseDto;
import com.storycraft.reward.entity.StreakStatus;
import com.storycraft.reward.entity.RewardPoint;
import com.storycraft.reward.entity.RewardBadge;
import com.storycraft.reward.repository.StreakStatusRepository;
import com.storycraft.reward.repository.RewardPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RewardStreakService {
    private final StreakStatusRepository streakStatusRepository;
    private final RewardPointRepository rewardPointRepository;
    private final ChildProfileRepository childProfileRepository;
    private final RewardBadgeService rewardBadgeService;

    @Transactional
    public StreakCheckResponseDto checkStreak(String userEmail, StreakCheckRequestDto request) {
        if (request.getChildId() == null) {
            throw new IllegalArgumentException("childId는 필수입니다.");
        }
        ChildProfile child = childProfileRepository.findById(request.getChildId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자녀입니다."));
        // TODO: userEmail과 child.user.email 일치 여부 체크(보안)

        LocalDate today = LocalDate.now();
        StreakStatus streak = streakStatusRepository.findByChild(child)
                .orElse(StreakStatus.builder()
                        .child(child)
                        .currentStreak(0)
                        .lastLearnedDate(today.minusDays(1))
                        .build());

        boolean alreadyCheckedToday = streak.getLastLearnedDate().isEqual(today);
        int rewardedPoint = 0;
        boolean streakRewarded = false;
        int newStreak = streak.getCurrentStreak();

        if (!alreadyCheckedToday) {
            // streak 계산
            if (streak.getLastLearnedDate().plusDays(1).isEqual(today)) {
                newStreak = streak.getCurrentStreak() + 1;
            } else {
                newStreak = 1;
            }
            streak.setCurrentStreak(newStreak);
            streak.setLastLearnedDate(today);
            streakStatusRepository.save(streak);

            // streak 보상 지급 구간 체크
            switch (newStreak) {
                case 3 -> {
                    rewardedPoint = 50;
                    streakRewarded = true;
                    grantStreakReward(child, rewardedPoint, "POINT_STREAK_3", "STREAK_3_DAYS", "BADGE_STREAK_3");
                }
                case 7 -> {
                    rewardedPoint = 100;
                    streakRewarded = true;
                    grantStreakReward(child, rewardedPoint, "POINT_STREAK_7", "STREAK_7_DAYS", "BADGE_STREAK_7");
                }
                case 14 -> {
                    rewardedPoint = 200;
                    streakRewarded = true;
                    grantStreakReward(child, rewardedPoint, "POINT_STREAK_14", "STREAK_14_DAYS", "BADGE_STREAK_14");
                }
                case 30 -> {
                    rewardedPoint = 300;
                    streakRewarded = true;
                    grantStreakReward(child, rewardedPoint, "POINT_STREAK_30", "STREAK_30_DAYS", "BADGE_STREAK_30");
                }
            }
        }
        return new StreakCheckResponseDto(newStreak, streakRewarded, rewardedPoint);
    }

    private void grantStreakReward(ChildProfile child, int point, String rewardType, String context, String badgeCode) {
        // 포인트 지급
        RewardPoint rewardPoint = RewardPoint.builder()
                .child(child)
                .rewardType(rewardType)
                .context(context)
                .points(point)
                .build();
        rewardPointRepository.save(rewardPoint);
        // streak 배지 지급
        rewardBadgeService.grantBadge(child, badgeCode);
    }
} 