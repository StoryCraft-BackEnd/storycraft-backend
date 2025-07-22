package com.storycraft.reward.controller;

import com.storycraft.auth.jwt.SecurityUtil;
import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.reward.dto.LevelUpCheckRequestDto;
import com.storycraft.reward.dto.LevelUpCheckResponseDto;
import com.storycraft.reward.service.RewardLevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rewards/check-level-up")
@RequiredArgsConstructor
public class RewardLevelController {
    private final RewardLevelService rewardLevelService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<LevelUpCheckResponseDto>> checkLevelUp(@RequestBody LevelUpCheckRequestDto request) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        LevelUpCheckResponseDto response = rewardLevelService.checkLevelUp(userEmail, request);
        String message = response.isLevelUp() ? "레벨업 성공" : "레벨 변화 없음";
        return ResponseEntity.ok(new ApiResponseDto<>(200, message, response));
    }
} 