package com.storycraft.reward.service;

import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.profile.repository.ChildProfileRepository;
import com.storycraft.reward.dto.LevelUpCheckRequestDto;
import com.storycraft.reward.dto.LevelUpCheckResponseDto;
import com.storycraft.reward.repository.RewardPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.storycraft.reward.entity.RewardPoint;

@Service
@RequiredArgsConstructor
public class RewardLevelService {
    private final ChildProfileRepository childProfileRepository;
    private final RewardPointRepository rewardPointRepository;
    private final RewardBadgeService rewardBadgeService;

    // 누적 포인트 500점마다 1레벨 상승
    private static final int POINTS_PER_LEVEL = 500;

    @Transactional
    public LevelUpCheckResponseDto checkLevelUp(String userEmail, LevelUpCheckRequestDto request) {
        return checkLevelUp(userEmail, request, false);
    }

    @Transactional
    public LevelUpCheckResponseDto checkLevelUp(String userEmail, LevelUpCheckRequestDto request, boolean skipLevelUpRecord) {
        if (request.getChildId() == null) {
            throw new IllegalArgumentException("childId는 필수입니다.");
        }
        ChildProfile child = childProfileRepository.findById(request.getChildId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자녀입니다."));
        // TODO: userEmail과 child.user.email 일치 여부 체크(보안)

        int totalPoint = rewardPointRepository.sumPointsByChild(child);
        int oldLevel = child.getAge() != null ? child.getAge() : 1; // 기존 레벨(임시: age 필드 사용, 실제는 level 필드 필요)
        int newLevel = calculateLevel(totalPoint);
        boolean levelUp = newLevel > oldLevel;

        if (levelUp) {
            // 레벨 업데이트
            child.setAge(newLevel); // 실제로는 setLevel() 필드 필요
            childProfileRepository.save(child);
            
            // 레벨업 추적용 포인트 기록 (실제 포인트 지급 없음) - skipLevelUpRecord가 false일 때만
            if (!skipLevelUpRecord) {
                RewardPoint levelUpRecord = RewardPoint.builder()
                        .child(child)
                        .rewardType("LEVEL_UP")
                        .context("LEVEL_CHECK")
                        .points(0)
                        .build();
                rewardPointRepository.save(levelUpRecord);
            }
            
            // 레벨업 배지 지급
            if (newLevel == 1) rewardBadgeService.grantBadge(child, "BADGE_LEVEL_1");
            if (newLevel == 5) rewardBadgeService.grantBadge(child, "BADGE_LEVEL_5");
            if (newLevel == 10) rewardBadgeService.grantBadge(child, "BADGE_LEVEL_10");
        }
        return new LevelUpCheckResponseDto(levelUp, newLevel);
    }

    private int calculateLevel(int totalPoint) {
        // 누적 포인트 500점마다 1레벨 상승
        return (totalPoint / POINTS_PER_LEVEL) + 1;
    }
} 