package com.storycraft.reward.service;

import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.profile.repository.ChildProfileRepository;
import com.storycraft.reward.dto.BadgeCheckRequestDto;
import com.storycraft.reward.dto.BadgeCheckResponseDto;
import com.storycraft.reward.entity.RewardBadge;
import com.storycraft.reward.repository.RewardPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardBadgeCheckService {
    private final ChildProfileRepository childProfileRepository;
    private final RewardPointRepository rewardPointRepository;
    private final RewardBadgeService rewardBadgeService;

    @Transactional
    public BadgeCheckResponseDto checkAndGrantBadges(String userEmail, BadgeCheckRequestDto request) {
        if (request.getChildId() == null) {
            throw new IllegalArgumentException("childId는 필수입니다.");
        }
        ChildProfile child = childProfileRepository.findById(request.getChildId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자녀입니다."));
        // TODO: userEmail과 child.user.email 일치 여부 체크(보안)

        List<BadgeCheckResponseDto.NewBadgeDto> newBadges = new ArrayList<>();

        switch (request.getActivityType()) {
            case "STORY_READ" -> newBadges.addAll(checkStoryBadges(child));
            case "WORD_CLICK" -> newBadges.addAll(checkWordBadges(child));
            case "QUIZ_CORRECT" -> newBadges.addAll(checkQuizBadges(child));
            case "DAILY_MISSION" -> newBadges.addAll(checkDailyMissionBadges(child));
            case "STREAK" -> newBadges.addAll(checkStreakBadges(child));
            case "LEVEL_UP" -> newBadges.addAll(checkLevelBadges(child));
            default -> throw new IllegalArgumentException("유효하지 않은 activityType입니다: " + request.getActivityType());
        }

        return new BadgeCheckResponseDto(newBadges);
    }

    private List<BadgeCheckResponseDto.NewBadgeDto> checkStoryBadges(ChildProfile child) {
        List<BadgeCheckResponseDto.NewBadgeDto> newBadges = new ArrayList<>();
        
        // 동화 읽기 횟수 계산
        int storyReadCount = rewardPointRepository.countByChildAndRewardType(child, "POINT_STORY_READ");
        
        // 첫 번째 동화 읽기
        if (storyReadCount == 1 && !rewardBadgeService.hasBadge(child, "BADGE_STORY_1")) {
            RewardBadge badge = rewardBadgeService.grantBadge(child, "BADGE_STORY_1");
            if (badge != null) {
                newBadges.add(new BadgeCheckResponseDto.NewBadgeDto(badge.getBadgeCode(), badge.getBadgeName()));
            }
        }
        
        // 동화 10편 읽기
        if (storyReadCount == 10 && !rewardBadgeService.hasBadge(child, "BADGE_STORY_10")) {
            RewardBadge badge = rewardBadgeService.grantBadge(child, "BADGE_STORY_10");
            if (badge != null) {
                newBadges.add(new BadgeCheckResponseDto.NewBadgeDto(badge.getBadgeCode(), badge.getBadgeName()));
            }
        }
        
        // 동화 50편 읽기
        if (storyReadCount == 50 && !rewardBadgeService.hasBadge(child, "BADGE_STORY_50")) {
            RewardBadge badge = rewardBadgeService.grantBadge(child, "BADGE_STORY_50");
            if (badge != null) {
                newBadges.add(new BadgeCheckResponseDto.NewBadgeDto(badge.getBadgeCode(), badge.getBadgeName()));
            }
        }
        
        return newBadges;
    }

    private List<BadgeCheckResponseDto.NewBadgeDto> checkWordBadges(ChildProfile child) {
        List<BadgeCheckResponseDto.NewBadgeDto> newBadges = new ArrayList<>();
        
        // 단어 클릭 횟수 계산
        int wordClickCount = rewardPointRepository.countByChildAndRewardType(child, "POINT_WORD_CLICK");
        
        // 첫 단어 클릭
        if (wordClickCount == 1 && !rewardBadgeService.hasBadge(child, "BADGE_WORD_1")) {
            RewardBadge badge = rewardBadgeService.grantBadge(child, "BADGE_WORD_1");
            if (badge != null) {
                newBadges.add(new BadgeCheckResponseDto.NewBadgeDto(badge.getBadgeCode(), badge.getBadgeName()));
            }
        }
        
        // 단어 100개 클릭
        if (wordClickCount == 100 && !rewardBadgeService.hasBadge(child, "BADGE_WORD_100")) {
            RewardBadge badge = rewardBadgeService.grantBadge(child, "BADGE_WORD_100");
            if (badge != null) {
                newBadges.add(new BadgeCheckResponseDto.NewBadgeDto(badge.getBadgeCode(), badge.getBadgeName()));
            }
        }
        
        // 단어 500개 클릭
        if (wordClickCount == 500 && !rewardBadgeService.hasBadge(child, "BADGE_WORD_500")) {
            RewardBadge badge = rewardBadgeService.grantBadge(child, "BADGE_WORD_500");
            if (badge != null) {
                newBadges.add(new BadgeCheckResponseDto.NewBadgeDto(badge.getBadgeCode(), badge.getBadgeName()));
            }
        }
        
        return newBadges;
    }

    private List<BadgeCheckResponseDto.NewBadgeDto> checkQuizBadges(ChildProfile child) {
        List<BadgeCheckResponseDto.NewBadgeDto> newBadges = new ArrayList<>();
        
        // 퀴즈 정답 횟수 계산
        int quizCorrectCount = rewardPointRepository.countByChildAndRewardType(child, "POINT_QUIZ_CORRECT");
        
        // 첫 퀴즈 정답
        if (quizCorrectCount == 1 && !rewardBadgeService.hasBadge(child, "BADGE_QUIZ_1")) {
            RewardBadge badge = rewardBadgeService.grantBadge(child, "BADGE_QUIZ_1");
            if (badge != null) {
                newBadges.add(new BadgeCheckResponseDto.NewBadgeDto(badge.getBadgeCode(), badge.getBadgeName()));
            }
        }
        
        // 퀴즈 10개 정답
        if (quizCorrectCount == 10 && !rewardBadgeService.hasBadge(child, "BADGE_QUIZ_10")) {
            RewardBadge badge = rewardBadgeService.grantBadge(child, "BADGE_QUIZ_10");
            if (badge != null) {
                newBadges.add(new BadgeCheckResponseDto.NewBadgeDto(badge.getBadgeCode(), badge.getBadgeName()));
            }
        }
        
        // 퀴즈 50개 정답
        if (quizCorrectCount == 50 && !rewardBadgeService.hasBadge(child, "BADGE_QUIZ_50")) {
            RewardBadge badge = rewardBadgeService.grantBadge(child, "BADGE_QUIZ_50");
            if (badge != null) {
                newBadges.add(new BadgeCheckResponseDto.NewBadgeDto(badge.getBadgeCode(), badge.getBadgeName()));
            }
        }
        
        return newBadges;
    }

    private List<BadgeCheckResponseDto.NewBadgeDto> checkDailyMissionBadges(ChildProfile child) {
        List<BadgeCheckResponseDto.NewBadgeDto> newBadges = new ArrayList<>();
        
        // 데일리 미션 완료 횟수 계산
        int dailyMissionCount = rewardPointRepository.countByChildAndRewardType(child, "POINT_DAILY_MISSION");
        
        // 데일리 마스터 7일 연속
        if (dailyMissionCount == 7 && !rewardBadgeService.hasBadge(child, "BADGE_DAILY_7")) {
            RewardBadge badge = rewardBadgeService.grantBadge(child, "BADGE_DAILY_7");
            if (badge != null) {
                newBadges.add(new BadgeCheckResponseDto.NewBadgeDto(badge.getBadgeCode(), badge.getBadgeName()));
            }
        }
        
        return newBadges;
    }

    private List<BadgeCheckResponseDto.NewBadgeDto> checkStreakBadges(ChildProfile child) {
        List<BadgeCheckResponseDto.NewBadgeDto> newBadges = new ArrayList<>();
        
        // streak 관련 배지들은 이미 RewardStreakService에서 처리되므로 여기서는 체크만
        if (!rewardBadgeService.hasBadge(child, "BADGE_STREAK_3")) {
            RewardBadge badge = rewardBadgeService.grantBadge(child, "BADGE_STREAK_3");
            if (badge != null) {
                newBadges.add(new BadgeCheckResponseDto.NewBadgeDto(badge.getBadgeCode(), badge.getBadgeName()));
            }
        }
        
        if (!rewardBadgeService.hasBadge(child, "BADGE_STREAK_7")) {
            RewardBadge badge = rewardBadgeService.grantBadge(child, "BADGE_STREAK_7");
            if (badge != null) {
                newBadges.add(new BadgeCheckResponseDto.NewBadgeDto(badge.getBadgeCode(), badge.getBadgeName()));
            }
        }
        
        if (!rewardBadgeService.hasBadge(child, "BADGE_STREAK_14")) {
            RewardBadge badge = rewardBadgeService.grantBadge(child, "BADGE_STREAK_14");
            if (badge != null) {
                newBadges.add(new BadgeCheckResponseDto.NewBadgeDto(badge.getBadgeCode(), badge.getBadgeName()));
            }
        }
        
        if (!rewardBadgeService.hasBadge(child, "BADGE_STREAK_30")) {
            RewardBadge badge = rewardBadgeService.grantBadge(child, "BADGE_STREAK_30");
            if (badge != null) {
                newBadges.add(new BadgeCheckResponseDto.NewBadgeDto(badge.getBadgeCode(), badge.getBadgeName()));
            }
        }
        
        return newBadges;
    }

    private List<BadgeCheckResponseDto.NewBadgeDto> checkLevelBadges(ChildProfile child) {
        List<BadgeCheckResponseDto.NewBadgeDto> newBadges = new ArrayList<>();
        
        int currentLevel = child.getAge() != null ? child.getAge() : 1;
        
        if (currentLevel == 1 && !rewardBadgeService.hasBadge(child, "BADGE_LEVEL_1")) {
            RewardBadge badge = rewardBadgeService.grantBadge(child, "BADGE_LEVEL_1");
            if (badge != null) {
                newBadges.add(new BadgeCheckResponseDto.NewBadgeDto(badge.getBadgeCode(), badge.getBadgeName()));
            }
        }
        
        if (currentLevel == 5 && !rewardBadgeService.hasBadge(child, "BADGE_LEVEL_5")) {
            RewardBadge badge = rewardBadgeService.grantBadge(child, "BADGE_LEVEL_5");
            if (badge != null) {
                newBadges.add(new BadgeCheckResponseDto.NewBadgeDto(badge.getBadgeCode(), badge.getBadgeName()));
            }
        }
        
        if (currentLevel == 10 && !rewardBadgeService.hasBadge(child, "BADGE_LEVEL_10")) {
            RewardBadge badge = rewardBadgeService.grantBadge(child, "BADGE_LEVEL_10");
            if (badge != null) {
                newBadges.add(new BadgeCheckResponseDto.NewBadgeDto(badge.getBadgeCode(), badge.getBadgeName()));
            }
        }
        
        return newBadges;
    }
} 