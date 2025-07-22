package com.storycraft.reward.controller;

import com.storycraft.auth.jwt.SecurityUtil;
import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.reward.dto.DailyMissionStatusDto;
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
} 