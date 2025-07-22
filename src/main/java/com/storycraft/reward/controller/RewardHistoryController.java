package com.storycraft.reward.controller;

import com.storycraft.auth.jwt.SecurityUtil;
import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.reward.dto.RewardHistoryItemDto;
import com.storycraft.reward.service.RewardHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/rewards/history")
@RequiredArgsConstructor
public class RewardHistoryController {
    private final RewardHistoryService rewardHistoryService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<RewardHistoryItemDto>>> getHistory(
            @RequestParam Long childId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String type
    ) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        // TODO: userEmail과 childId 소유권 체크(보안)
        List<RewardHistoryItemDto> history = rewardHistoryService.getHistory(childId, type, from, to);
        return ResponseEntity.ok(new ApiResponseDto<>(200, "보상 히스토리 조회 완료", history));
    }
} 