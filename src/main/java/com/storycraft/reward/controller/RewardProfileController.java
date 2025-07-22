package com.storycraft.reward.controller;

import com.storycraft.auth.jwt.SecurityUtil;
import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.reward.dto.RewardProfileResponseDto;
import com.storycraft.reward.service.RewardProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rewards/profiles")
@RequiredArgsConstructor
public class RewardProfileController {
    private final RewardProfileService rewardProfileService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<RewardProfileResponseDto>> getRewardProfile(@RequestParam Long childId) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        // TODO: userEmail과 childId 소유권 체크(보안)
        
        RewardProfileResponseDto profile = rewardProfileService.getRewardProfile(childId);
        return ResponseEntity.ok(new ApiResponseDto<>(200, "보상 현황 조회 완료.", profile));
    }
} 