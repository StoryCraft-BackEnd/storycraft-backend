package com.storycraft.reward.controller;

import com.storycraft.auth.jwt.SecurityUtil;
import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.reward.dto.RewardPointGrantRequestDto;
import com.storycraft.reward.dto.RewardPointGrantResponseDto;
import com.storycraft.reward.service.RewardPointService;
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
@RequestMapping("/rewards/points")
@RequiredArgsConstructor
@Tag(name = "Rewards", description = "보상 시스템 관련 API")
public class RewardPointController {
    private final RewardPointService rewardPointService;

    @Operation(
        summary = "포인트 지급", 
        description = "학습 활동에 따른 포인트를 지급하고, 레벨업과 배지 획득을 자동으로 체크합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "포인트 지급 완료",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RewardPointGrantResponseDto.class),
                examples = {
                    @ExampleObject(
                        name = "동화 읽기 포인트 지급",
                        value = """
                        {
                          "status": 200,
                          "message": "포인트 지급 완료, 레벨업 성공, 배지 획득",
                          "data": {
                            "rewardedPoint": 30,
                            "totalPoint": 500,
                            "levelUp": {
                              "levelUp": true,
                              "newLevel": 2
                            },
                            "newBadges": [
                              {
                                "badgeCode": "BADGE_STORY_1",
                                "badgeName": "첫 번째 동화 읽기"
                              }
                            ]
                          }
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "단어 클릭 포인트 지급",
                        value = """
                        {
                          "status": 200,
                          "message": "포인트 지급 완료",
                          "data": {
                            "rewardedPoint": 5,
                            "totalPoint": 35,
                            "levelUp": null,
                            "newBadges": []
                          }
                        }
                        """
                    )
                }
            )
        ),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (childId 누락, 유효하지 않은 rewardType 등)")
    })
    @PostMapping
    public ResponseEntity<ApiResponseDto<RewardPointGrantResponseDto>> grantPoint(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "포인트 지급 요청",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RewardPointGrantRequestDto.class),
                examples = {
                    @ExampleObject(
                        name = "동화 읽기 완료",
                        value = """
                        {
                          "childId": 1,
                          "rewardType": "POINT_STORY_READ",
                          "context": "STORY_READ"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "단어 클릭",
                        value = """
                        {
                          "childId": 1,
                          "rewardType": "POINT_WORD_CLICK",
                          "context": "WORD_CLICK"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "퀴즈 정답",
                        value = """
                        {
                          "childId": 1,
                          "rewardType": "POINT_QUIZ_CORRECT",
                          "context": "QUIZ_CORRECT"
                        }
                        """
                    )
                }
            )
        )
        @RequestBody RewardPointGrantRequestDto request
    ) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        RewardPointGrantResponseDto response = rewardPointService.grantPoint(userEmail, request);
        
        StringBuilder message = new StringBuilder("포인트 지급 완료");
        if (response.getLevelUp() != null && response.getLevelUp().isLevelUp()) {
            message.append(", 레벨업 성공");
        }
        if (response.getNewBadges() != null && !response.getNewBadges().isEmpty()) {
            message.append(", 배지 획득");
        }
        
        return ResponseEntity.ok(new ApiResponseDto<>(200, message.toString(), response));
    }
} 