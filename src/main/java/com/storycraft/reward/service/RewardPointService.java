package com.storycraft.reward.service;

import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.profile.repository.ChildProfileRepository;
import com.storycraft.reward.dto.RewardPointGrantRequestDto;
import com.storycraft.reward.dto.RewardPointGrantResponseDto;
import com.storycraft.reward.entity.RewardPoint;
import com.storycraft.reward.repository.RewardPointRepository;
import com.storycraft.reward.dto.LevelUpCheckRequestDto;
import com.storycraft.reward.dto.LevelUpCheckResponseDto;
import com.storycraft.reward.service.RewardLevelService;
import com.storycraft.reward.service.RewardBadgeCheckService;
import com.storycraft.reward.dto.BadgeCheckRequestDto;
import com.storycraft.reward.dto.BadgeCheckResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardPointService {
    private final RewardPointRepository rewardPointRepository;
    private final ChildProfileRepository childProfileRepository;
    private final RewardLevelService rewardLevelService;
    private final RewardBadgeCheckService rewardBadgeCheckService; // 추가

    @Transactional
    public RewardPointGrantResponseDto grantPoint(String userEmail, RewardPointGrantRequestDto request) {
        // childId는 request에서 받거나, userEmail로부터 추출(보호자-자녀 구조에 따라 확장 가능)
        if (request.getChildId() == null) {
            throw new IllegalArgumentException("childId는 필수입니다.");
        }
        ChildProfile child = childProfileRepository.findById(request.getChildId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자녀입니다."));
        // TODO: userEmail과 child.user.email 일치 여부 체크(보안)

        // 포인트 지급
        int points = getPointValue(request.getRewardType());
        if (points == 0 && !"LEVEL_UP".equals(request.getRewardType())) {
            throw new IllegalArgumentException("유효하지 않은 rewardType입니다: " + request.getRewardType());
        }

        RewardPoint rewardPoint = RewardPoint.builder()
                .child(child)
                .rewardType(request.getRewardType())
                .context(request.getContext())
                .points(points)
                .build();
        rewardPointRepository.save(rewardPoint);

        int totalPoint = rewardPointRepository.sumPointsByChild(child);
        
        // 포인트 지급 후 자동 레벨업 체크 (LEVEL_UP 타입이 아닐 때만)
        LevelUpCheckResponseDto levelUp = null;
        if (!"LEVEL_UP".equals(request.getRewardType())) {
            levelUp = rewardLevelService.checkLevelUp(userEmail, 
                new LevelUpCheckRequestDto(request.getChildId()), true); // skipLevelUpRecord = true
        }
        
        // 레벨업 후 자동 배지 체크 (LEVEL_UP 타입이 아닐 때만)
        List<BadgeCheckResponseDto.NewBadgeDto> newBadges = null;
        if (!"LEVEL_UP".equals(request.getRewardType())) {
            BadgeCheckRequestDto badgeRequest = new BadgeCheckRequestDto();
            badgeRequest.setChildId(request.getChildId());
            badgeRequest.setActivityType(getActivityTypeFromRewardType(request.getRewardType()));
            badgeRequest.setTargetId(request.getTargetId());
            
            BadgeCheckResponseDto badgeResponse = rewardBadgeCheckService.checkAndGrantBadges(userEmail, badgeRequest);
            newBadges = badgeResponse.getNewBadges();
        }
        
        return new RewardPointGrantResponseDto(rewardPoint.getPoints(), totalPoint, levelUp, newBadges);
    }

    private int getPointValue(String rewardType) {
        return switch (rewardType) {
            case "POINT_STORY_READ" -> 30;
            case "POINT_WORD_CLICK" -> 5;
            case "POINT_QUIZ_CORRECT" -> 10;
            case "POINT_DAILY_MISSION" -> 100;
            case "POINT_STREAK_3" -> 50;
            case "POINT_STREAK_7" -> 100;
            case "POINT_STREAK_14" -> 200;
            case "LEVEL_UP" -> 0; // 레벨업 추적용, 포인트 지급 없음
            default -> 0;
        };
    }

    // rewardType을 activityType으로 변환
    private String getActivityTypeFromRewardType(String rewardType) {
        return switch (rewardType) {
            case "POINT_STORY_READ" -> "STORY_READ";
            case "POINT_WORD_CLICK" -> "WORD_CLICK";
            case "POINT_QUIZ_CORRECT" -> "QUIZ_CORRECT";
            case "POINT_DAILY_MISSION" -> "DAILY_MISSION";
            case "POINT_STREAK_3", "POINT_STREAK_7", "POINT_STREAK_14", "POINT_STREAK_30" -> "STREAK";
            default -> "GENERAL";
        };
    }

    /**
     * 유효한 rewardType 목록 반환 (문서화/검증용)
     */
    public static String[] getValidRewardTypes() {
        return new String[]{
                "POINT_STORY_READ",      // 동화 1편 읽기 완료 (+30pt)
                "POINT_WORD_CLICK",      // 단어 1회 클릭 (+5pt)
                "POINT_QUIZ_CORRECT",    // 퀴즈 1문제 정답 (+10pt)
                "POINT_DAILY_MISSION",   // 데일리 미션 전체 달성 (+100pt)
                "POINT_STREAK_3",        // 3일 연속 학습 (+50pt)
                "POINT_STREAK_7",        // 7일 연속 학습 (+100pt)
                "POINT_STREAK_14",       // 14일 연속 학습 (+200pt)
                "LEVEL_UP"               // 누적 포인트 500점 달성마다 1레벨 상승 (포인트 지급 없음)
        };
    }
} 