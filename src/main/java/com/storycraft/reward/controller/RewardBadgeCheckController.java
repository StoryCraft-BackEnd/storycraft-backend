package com.storycraft.reward.controller;

import com.storycraft.auth.jwt.SecurityUtil;
import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.reward.dto.BadgeCheckRequestDto;
import com.storycraft.reward.dto.BadgeCheckResponseDto;
import com.storycraft.reward.service.RewardBadgeCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rewards/check-badges")
@RequiredArgsConstructor
public class RewardBadgeCheckController {
    private final RewardBadgeCheckService rewardBadgeCheckService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<BadgeCheckResponseDto>> checkBadges(@RequestBody BadgeCheckRequestDto request) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        BadgeCheckResponseDto response = rewardBadgeCheckService.checkAndGrantBadges(userEmail, request);
        String message = response.getNewBadges().isEmpty() ? "새로 지급된 배지 없음" : "배지 지급 완료";
        return ResponseEntity.ok(new ApiResponseDto<>(200, message, response));
    }
} 