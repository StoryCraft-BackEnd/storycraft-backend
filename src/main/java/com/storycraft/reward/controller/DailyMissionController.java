package com.storycraft.reward.controller;

import com.storycraft.auth.jwt.SecurityUtil;
import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.reward.dto.DailyMissionStatusDto;
import com.storycraft.reward.dto.DailyMissionCheckResponseDto;
import com.storycraft.reward.dto.DailyMissionCheckRequestDto;
import com.storycraft.reward.service.DailyMissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rewards/daily-mission")
@RequiredArgsConstructor
public class DailyMissionController {
    private final DailyMissionService dailyMissionService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<DailyMissionStatusDto>>> getDailyMissionStatus(@RequestParam Long childId) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        // TODO: userEmail과 childId 소유권 체크(보안)
        
        List<DailyMissionStatusDto> missionStatuses = dailyMissionService.getDailyMissionStatus(childId);
        return ResponseEntity.ok(new ApiResponseDto<>(200, "데일리 미션 상태 조회 완료", missionStatuses));
    }

    @PostMapping("/check-daily-mission")
    public ResponseEntity<ApiResponseDto<DailyMissionCheckResponseDto>> checkAndClaimDailyMission(@RequestBody DailyMissionCheckRequestDto request) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        DailyMissionCheckResponseDto response = dailyMissionService.checkAndClaimDailyMission(userEmail, request.getChildId());
        String message = response.isAlreadyClaimed() ? "이미 오늘 데일리 미션 보상을 받았습니다." :
                (response.getRewardedPoint() > 0 ? "데일리 미션 완료! 100포인트 지급 완료." : "아직 데일리 미션을 모두 완료하지 않았습니다.");
        return ResponseEntity.ok(new ApiResponseDto<>(200, message, response));
    }
} 