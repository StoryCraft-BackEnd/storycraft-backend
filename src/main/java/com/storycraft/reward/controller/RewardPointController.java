package com.storycraft.reward.controller;

import com.storycraft.auth.jwt.SecurityUtil;
import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.reward.dto.RewardPointGrantRequestDto;
import com.storycraft.reward.dto.RewardPointGrantResponseDto;
import com.storycraft.reward.service.RewardPointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rewards/points")
@RequiredArgsConstructor
public class RewardPointController {
    private final RewardPointService rewardPointService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<RewardPointGrantResponseDto>> grantPoint(@RequestBody RewardPointGrantRequestDto request) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        RewardPointGrantResponseDto response = rewardPointService.grantPoint(userEmail, request);
        return ResponseEntity.ok(new ApiResponseDto<>(200, "포인트 지급 완료.", response));
    }
} 