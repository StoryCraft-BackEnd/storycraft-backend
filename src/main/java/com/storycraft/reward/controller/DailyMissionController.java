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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;

@RestController
@RequestMapping("/rewards/daily-mission")
@RequiredArgsConstructor
@Tag(name = "Rewards", description = "보상 시스템 관련 API")
public class DailyMissionController {
    private final DailyMissionService dailyMissionService;

    @Operation(
        summary = "데일리 미션 상태 조회", 
        description = "오늘의 데일리 미션 진행 상태를 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "데일리 미션 상태 조회 완료",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = DailyMissionStatusDto.class),
                examples = {
                    @ExampleObject(
                        name = "데일리 미션 상태",
                        value = """
                        {
                          "status": 200,
                          "message": "데일리 미션 상태 조회 완료",
                          "data": [
                            {
                              "missionCode": "MISSION_STORY_READ",
                              "missionName": "동화 1편 읽기",
                              "progressCount": 1,
                              "targetCount": 1,
                              "completed": true
                            },
                            {
                              "missionCode": "MISSION_WORD_CLICK",
                              "missionName": "단어 10개 클릭",
                              "progressCount": 8,
                              "targetCount": 10,
                              "completed": false
                            },
                            {
                              "missionCode": "MISSION_QUIZ_CORRECT",
                              "missionName": "퀴즈 10개 정답",
                              "progressCount": 10,
                              "targetCount": 10,
                              "completed": true
                            }
                          ]
                        }
                        """
                    )
                }
            )
        )
    })
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<DailyMissionStatusDto>>> getDailyMissionStatus(
        @Parameter(description = "자녀 ID", example = "1") @RequestParam Long childId
    ) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        // TODO: userEmail과 childId 소유권 체크(보안)
        
        List<DailyMissionStatusDto> missionStatuses = dailyMissionService.getDailyMissionStatus(childId);
        return ResponseEntity.ok(new ApiResponseDto<>(200, "데일리 미션 상태 조회 완료", missionStatuses));
    }

    @Operation(
        summary = "데일리 미션 조건 판단", 
        description = "데일리 미션 3가지(동화 1편 읽기, 단어 10개 클릭, 퀴즈 10개 정답) 모두 달성했는지 확인하고, 완료 시 100포인트를 지급합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "데일리 미션 체크 완료",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = DailyMissionCheckResponseDto.class),
                examples = {
                    @ExampleObject(
                        name = "데일리 미션 완료 및 보상 지급",
                        value = """
                        {
                          "status": 200,
                          "message": "데일리 미션 완료! 100포인트 지급 완료.",
                          "data": {
                            "rewardedPoint": 100,
                            "alreadyClaimed": false
                          }
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "이미 보상 받음",
                        value = """
                        {
                          "status": 200,
                          "message": "이미 오늘 데일리 미션 보상을 받았습니다.",
                          "data": {
                            "rewardedPoint": 0,
                            "alreadyClaimed": true
                          }
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "미션 미완료",
                        value = """
                        {
                          "status": 200,
                          "message": "아직 데일리 미션을 모두 완료하지 않았습니다.",
                          "data": {
                            "rewardedPoint": 0,
                            "alreadyClaimed": false
                          }
                        }
                        """
                    )
                }
            )
        )
    })
    @PostMapping("/check-daily-mission")
    public ResponseEntity<ApiResponseDto<DailyMissionCheckResponseDto>> checkAndClaimDailyMission(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "데일리 미션 체크 요청",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = DailyMissionCheckRequestDto.class),
                examples = {
                    @ExampleObject(
                        name = "데일리 미션 체크",
                        value = """
                        {
                          "childId": 1
                        }
                        """
                    )
                }
            )
        )
        @RequestBody DailyMissionCheckRequestDto request
    ) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        DailyMissionCheckResponseDto response = dailyMissionService.checkAndClaimDailyMission(userEmail, request.getChildId());
        String message = response.isAlreadyClaimed() ? "이미 오늘 데일리 미션 보상을 받았습니다." :
                (response.getRewardedPoint() > 0 ? "데일리 미션 완료! 100포인트 지급 완료." : "아직 데일리 미션을 모두 완료하지 않았습니다.");
        return ResponseEntity.ok(new ApiResponseDto<>(200, message, response));
    }
} 