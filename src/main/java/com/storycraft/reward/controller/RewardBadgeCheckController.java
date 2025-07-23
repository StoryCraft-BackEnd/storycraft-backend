package com.storycraft.reward.controller;

import com.storycraft.auth.jwt.SecurityUtil;
import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.reward.dto.BadgeCheckRequestDto;
import com.storycraft.reward.dto.BadgeCheckResponseDto;
import com.storycraft.reward.service.RewardBadgeCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/rewards/check-badges")
@RequiredArgsConstructor
@Tag(name = "Rewards", description = "보상 시스템 관련 API")
@SecurityRequirement(name = "bearerAuth")
public class RewardBadgeCheckController {
    private final RewardBadgeCheckService rewardBadgeCheckService;

    @Operation(
        summary = "배지 조건 판단 및 지급 여부 확인", 
        description = "조건을 체크하고 해당하는 배지가 있으면 지급합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "배지 체크 완료",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BadgeCheckResponseDto.class),
                examples = {
                    @ExampleObject(
                        name = "새 배지 지급됨",
                        value = """
                        {
                          "status": 200,
                          "message": "배지 지급 완료",
                          "data": {
                            "newBadges": [
                              {
                                "badgeId": 1,
                                "badgeCode": "BADGE_STORY_READ_10",
                                "badgeName": "동화 읽기 마스터",
                                "awardedAt": "2025-01-15T10:30:00"
                              }
                            ]
                          }
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "새 배지 없음",
                        value = """
                        {
                          "status": 200,
                          "message": "새로 지급된 배지 없음",
                          "data": {
                            "newBadges": []
                          }
                        }
                        """
                    )
                }
            )
        )
    })
    @PostMapping
    public ResponseEntity<ApiResponseDto<BadgeCheckResponseDto>> checkBadges(@RequestBody BadgeCheckRequestDto request) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        BadgeCheckResponseDto response = rewardBadgeCheckService.checkAndGrantBadges(userEmail, request);
        String message = response.getNewBadges().isEmpty() ? "새로 지급된 배지 없음" : "배지 지급 완료";
        return ResponseEntity.ok(new ApiResponseDto<>(200, message, response));
    }
} 