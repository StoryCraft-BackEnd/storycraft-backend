package com.storycraft.reward.controller;

import com.storycraft.auth.jwt.SecurityUtil;
import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.reward.dto.LevelUpCheckRequestDto;
import com.storycraft.reward.dto.LevelUpCheckResponseDto;
import com.storycraft.reward.service.RewardLevelService;
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
@RequestMapping("/rewards/check-level-up")
@RequiredArgsConstructor
@Tag(name = "Rewards", description = "보상 시스템 관련 API")
public class RewardLevelController {
    private final RewardLevelService rewardLevelService;

    @Operation(
        summary = "레벨업 체크 및 보상", 
        description = "포인트 누적에 따른 레벨업 여부를 체크하고, 레벨업 시 보상을 지급합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "레벨업 체크 결과",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LevelUpCheckResponseDto.class),
                examples = {
                    @ExampleObject(
                        name = "레벨업 성공",
                        value = """
                        {
                          "status": 200,
                          "message": "레벨업 성공",
                          "data": {
                            "levelUp": true,
                            "newLevel": 3
                          }
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "레벨 변화 없음",
                        value = """
                        {
                          "status": 200,
                          "message": "레벨 변화 없음",
                          "data": {
                            "levelUp": false,
                            "newLevel": 2
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
    public ResponseEntity<ApiResponseDto<LevelUpCheckResponseDto>> checkLevelUp(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "레벨업 체크 요청",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LevelUpCheckRequestDto.class),
                examples = {
                    @ExampleObject(
                        name = "레벨업 체크",
                        value = """
                        {
                          "childId": 1
                        }
                        """
                    )
                }
            )
        )
        @RequestBody LevelUpCheckRequestDto request
    ) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        LevelUpCheckResponseDto response = rewardLevelService.checkLevelUp(userEmail, request);
        String message = response.isLevelUp() ? "레벨업 성공" : "레벨 변화 없음";
        return ResponseEntity.ok(new ApiResponseDto<>(200, message, response));
    }
} 