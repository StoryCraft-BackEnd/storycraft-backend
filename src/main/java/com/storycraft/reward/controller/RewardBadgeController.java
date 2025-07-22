package com.storycraft.reward.controller;

import com.storycraft.auth.jwt.SecurityUtil;
import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.profile.repository.ChildProfileRepository;
import com.storycraft.reward.dto.BadgeInfoDto;
import com.storycraft.reward.service.RewardBadgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rewards/badges")
@RequiredArgsConstructor
public class RewardBadgeController {
    private final RewardBadgeService rewardBadgeService;
    private final ChildProfileRepository childProfileRepository;

    @GetMapping("/available")
    public ResponseEntity<ApiResponseDto<List<BadgeInfoDto>>> getAllAvailableBadges() {
        List<BadgeInfoDto> badges = RewardBadgeService.BadgeDefinition.getAllBadges().stream()
                .map(badge -> BadgeInfoDto.builder()
                        .badgeCode(badge.getBadgeCode())
                        .badgeName(badge.getBadgeName())
                        .condition(badge.getCondition())
                        .category(getBadgeCategory(badge.getBadgeCode()))
                        .build())
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(new ApiResponseDto<>(200, "사용 가능한 배지 목록 조회 완료", badges));
    }

    @GetMapping("/child/{childId}")
    public ResponseEntity<ApiResponseDto<List<BadgeInfoDto>>> getChildBadges(@PathVariable Long childId) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        // TODO: userEmail과 childId 소유권 체크(보안)
        
        ChildProfile child = childProfileRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자녀입니다."));
        
        List<BadgeInfoDto> badges = rewardBadgeService.getChildBadges(child).stream()
                .map(badge -> BadgeInfoDto.builder()
                        .badgeCode(badge.getBadgeCode())
                        .badgeName(badge.getBadgeName())
                        .condition("") // 이미 획득한 배지는 조건 표시 불필요
                        .category(getBadgeCategory(badge.getBadgeCode()))
                        .build())
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(new ApiResponseDto<>(200, "자녀 배지 목록 조회 완료", badges));
    }

    private String getBadgeCategory(String badgeCode) {
        if (RewardBadgeService.BadgeDefinition.BASIC_LEARNING_BADGES.stream()
                .anyMatch(badge -> badge.getBadgeCode().equals(badgeCode))) {
            return "BASIC_LEARNING";
        } else if (RewardBadgeService.BadgeDefinition.MILESTONE_BADGES.stream()
                .anyMatch(badge -> badge.getBadgeCode().equals(badgeCode))) {
            return "MILESTONE";
        } else if (RewardBadgeService.BadgeDefinition.STREAK_BADGES.stream()
                .anyMatch(badge -> badge.getBadgeCode().equals(badgeCode))) {
            return "STREAK";
        } else if (RewardBadgeService.BadgeDefinition.SPECIAL_CHALLENGE_BADGES.stream()
                .anyMatch(badge -> badge.getBadgeCode().equals(badgeCode))) {
            return "SPECIAL_CHALLENGE";
        }
        return "UNKNOWN";
    }
} 