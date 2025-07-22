package com.storycraft.reward.controller;

import com.storycraft.auth.jwt.SecurityUtil;
import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.reward.dto.RewardProfileResponseDto;
import com.storycraft.reward.service.RewardProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/rewards/profiles")
@RequiredArgsConstructor
@Tag(name = "Rewards", description = "보상 시스템 관련 API")
public class RewardProfileController {
    private final RewardProfileService rewardProfileService;

    @Operation(
        summary = "보상 현황 조회", 
        description = "자녀의 전체 보상 현황(포인트, 레벨, 배지, streak, 데일리 미션 상태)을 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "보상 현황 조회 완료",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RewardProfileResponseDto.class),
                examples = {
                    @ExampleObject(
                        name = "보상 현황",
                        value = """
                        {
                          "status": 200,
                          "message": "보상 현황 조회 완료",
                          "data": {
                            "points": 1200,
                            "level": 5,
                            "badges": [
                              {
                                "id": 1,
                                "badgeCode": "BADGE_STORY_1",
                                "badgeName": "첫 번째 동화 읽기",
                                "awardedAt": "2025-01-15T10:35:00"
                              },
                              {
                                "id": 2,
                                "badgeCode": "BADGE_WORD_100",
                                "badgeName": "단어 수집가",
                                "awardedAt": "2025-01-14T15:20:00"
                              }
                            ],
                            "streakDays": 7,
                            "dailyMissionStatus": "completed"
                          }
                        }
                        """
                    )
                }
            )
        ),
        @ApiResponse(responseCode = "400", description = "존재하지 않는 자녀입니다.")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDto<RewardProfileResponseDto>> getRewardProfile(
        @Parameter(description = "자녀 ID", example = "1")
        @RequestParam Long childId
    ) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        RewardProfileResponseDto profile = rewardProfileService.getRewardProfile(childId);
        return ResponseEntity.ok(new ApiResponseDto<>(200, "보상 현황 조회 완료", profile));
    }
} 