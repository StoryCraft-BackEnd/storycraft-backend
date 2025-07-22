package com.storycraft.reward.controller;

import com.storycraft.auth.jwt.SecurityUtil;
import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.reward.dto.StreakCheckRequestDto;
import com.storycraft.reward.dto.StreakCheckResponseDto;
import com.storycraft.reward.service.RewardStreakService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/rewards/check-streak")
@RequiredArgsConstructor
@Tag(name = "Reward Streak", description = "연속 학습 보상 관련 API")
public class RewardStreakController {
    private final RewardStreakService rewardStreakService;

    @Operation(
        summary = "연속 학습 체크 및 보상", 
        description = "오늘 첫 학습 시 연속 학습 일수를 체크하고, streak 보상(포인트, 배지)을 자동으로 지급합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "연속 학습 보상 지급",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = StreakCheckResponseDto.class),
                examples = {
                    @ExampleObject(
                        name = "3일 연속 학습 달성",
                        value = """
                        {
                          "status": 200,
                          "message": "연속 학습 보상 지급",
                          "data": {
                            "currentStreak": 3,
                            "streakRewarded": true,
                            "rewardedPoint": 50
                          }
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "이미 오늘 학습 완료",
                        value = """
                        {
                          "status": 200,
                          "message": "연속 학습 보상 지급",
                          "data": {
                            "currentStreak": 5,
                            "streakRewarded": false,
                            "rewardedPoint": 0
                          }
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "7일 연속 학습 달성",
                        value = """
                        {
                          "status": 200,
                          "message": "연속 학습 보상 지급",
                          "data": {
                            "currentStreak": 7,
                            "streakRewarded": true,
                            "rewardedPoint": 100
                          }
                        }
                        """
                    )
                }
            )
        ),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (childId 누락 등)")
    })
    @PostMapping
    public ResponseEntity<ApiResponseDto<StreakCheckResponseDto>> checkStreak(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "연속 학습 체크 요청",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = StreakCheckRequestDto.class),
                examples = {
                    @ExampleObject(
                        name = "연속 학습 체크",
                        value = """
                        {
                          "childId": 1
                        }
                        """
                    )
                }
            )
        )
        @RequestBody StreakCheckRequestDto request
    ) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        StreakCheckResponseDto response = rewardStreakService.checkStreak(userEmail, request);
        return ResponseEntity.ok(new ApiResponseDto<>(200, "연속 학습 보상 지급", response));
    }
} 