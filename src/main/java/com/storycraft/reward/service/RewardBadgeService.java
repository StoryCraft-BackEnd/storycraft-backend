package com.storycraft.reward.service;

import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.reward.entity.RewardBadge;
import com.storycraft.reward.repository.RewardBadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardBadgeService {
    private final RewardBadgeRepository rewardBadgeRepository;

    /**
     * 배지 정의 - 모든 배지 정보를 포함
     */
    public static class BadgeDefinition {
        public static final List<BadgeInfo> BASIC_LEARNING_BADGES = Arrays.asList(
                new BadgeInfo("BADGE_STORY_1", "첫 번째 동화 읽기", "동화 1편 읽기"),
                new BadgeInfo("BADGE_WORD_1", "첫 단어 클릭", "단어 클릭 1회"),
                new BadgeInfo("BADGE_QUIZ_1", "첫 퀴즈 도전", "퀴즈 정답 1회"),
                new BadgeInfo("BADGE_LEVEL_1", "레벨 1 달성!", "레벨 1 도달 시"),
                new BadgeInfo("BADGE_LEVEL_5", "레벨 5 달성!", "누적 포인트 기준 레벨 5 도달"),
                new BadgeInfo("BADGE_LEVEL_10", "레벨 10 달성!", "누적 포인트 기준 레벨 10 도달")
        );

        public static final List<BadgeInfo> MILESTONE_BADGES = Arrays.asList(
                new BadgeInfo("BADGE_STORY_10", "동화 마스터 10편", "동화 10편 읽기"),
                new BadgeInfo("BADGE_STORY_50", "동화 챔피언 50편", "동화 50편 읽기"),
                new BadgeInfo("BADGE_WORD_100", "단어 수집가", "단어 100개 클릭"),
                new BadgeInfo("BADGE_WORD_500", "단어 탐험가", "단어 500개 클릭"),
                new BadgeInfo("BADGE_QUIZ_10", "퀴즈 도전자", "퀴즈 정답 10회"),
                new BadgeInfo("BADGE_QUIZ_50", "퀴즈 마스터", "퀴즈 정답 50회")
        );

        public static final List<BadgeInfo> STREAK_BADGES = Arrays.asList(
                new BadgeInfo("BADGE_STREAK_3", "3일 연속 학습", "3일 연속 학습"),
                new BadgeInfo("BADGE_STREAK_7", "7일 연속 학습", "7일 연속 학습"),
                new BadgeInfo("BADGE_STREAK_14", "열공 천재", "14일 연속 학습"),
                new BadgeInfo("BADGE_STREAK_30", "공부 습관왕", "30일 연속 학습")
        );

        public static final List<BadgeInfo> SPECIAL_CHALLENGE_BADGES = Arrays.asList(
                new BadgeInfo("BADGE_DAILY_7", "데일리 마스터 7일 연속", "데일리 미션 7일 연속 수행")
        );

        public static List<BadgeInfo> getAllBadges() {
            List<BadgeInfo> allBadges = new java.util.ArrayList<>();
            allBadges.addAll(BASIC_LEARNING_BADGES);
            allBadges.addAll(MILESTONE_BADGES);
            allBadges.addAll(STREAK_BADGES);
            allBadges.addAll(SPECIAL_CHALLENGE_BADGES);
            return allBadges;
        }
    }

    public static class BadgeInfo {
        private final String badgeCode;
        private final String badgeName;
        private final String condition;

        public BadgeInfo(String badgeCode, String badgeName, String condition) {
            this.badgeCode = badgeCode;
            this.badgeName = badgeName;
            this.condition = condition;
        }

        public String getBadgeCode() { return badgeCode; }
        public String getBadgeName() { return badgeName; }
        public String getCondition() { return condition; }
    }

    /**
     * 배지 지급
     */
    public RewardBadge grantBadge(ChildProfile child, String badgeCode) {
        // 이미 지급된 배지인지 확인
        if (rewardBadgeRepository.existsByChildAndBadgeCode(child, badgeCode)) {
            return null; // 이미 지급됨
        }

        // 배지 정보 찾기
        BadgeInfo badgeInfo = findBadgeInfo(badgeCode);
        if (badgeInfo == null) {
            throw new IllegalArgumentException("유효하지 않은 배지 코드입니다: " + badgeCode);
        }

        // 배지 지급
        RewardBadge rewardBadge = RewardBadge.builder()
                .child(child)
                .badgeCode(badgeInfo.getBadgeCode())
                .badgeName(badgeInfo.getBadgeName())
                .build();
        
        return rewardBadgeRepository.save(rewardBadge);
    }

    /**
     * 배지 코드로 배지 정보 찾기
     */
    private BadgeInfo findBadgeInfo(String badgeCode) {
        return BadgeDefinition.getAllBadges().stream()
                .filter(badge -> badge.getBadgeCode().equals(badgeCode))
                .findFirst()
                .orElse(null);
    }

    /**
     * 자녀가 보유한 배지 목록 조회
     */
    public List<RewardBadge> getChildBadges(ChildProfile child) {
        return rewardBadgeRepository.findAllByChild(child);
    }

    /**
     * 특정 배지 보유 여부 확인
     */
    public boolean hasBadge(ChildProfile child, String badgeCode) {
        return rewardBadgeRepository.existsByChildAndBadgeCode(child, badgeCode);
    }
} 