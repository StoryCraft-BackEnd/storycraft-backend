package com.storycraft.reward.controller;

import com.storycraft.auth.jwt.SecurityUtil;
import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.reward.dto.StreakCheckRequestDto;
import com.storycraft.reward.dto.StreakCheckResponseDto;
import com.storycraft.reward.service.RewardStreakService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rewards/check-streak")
@RequiredArgsConstructor
public class RewardStreakController {
    private final RewardStreakService rewardStreakService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<StreakCheckResponseDto>> checkStreak(@RequestBody StreakCheckRequestDto request) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        StreakCheckResponseDto response = rewardStreakService.checkStreak(userEmail, request);
        return ResponseEntity.ok(new ApiResponseDto<>(200, "연속 학습 보상 지급", response));
    }
} 