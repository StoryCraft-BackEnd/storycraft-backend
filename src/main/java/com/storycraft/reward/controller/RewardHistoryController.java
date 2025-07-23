package com.storycraft.reward.controller;

import com.storycraft.auth.jwt.SecurityUtil;
import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.reward.dto.RewardHistoryItemDto;
import com.storycraft.reward.service.RewardHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/rewards/history")
@RequiredArgsConstructor
@Tag(name = "Rewards", description = "보상 시스템 관련 API")
public class RewardHistoryController {
    private final RewardHistoryService rewardHistoryService;

    @Operation(
        summary = "보상 히스토리 조회", 
        description = "지정된 기간 동안의 포인트 지급 및 배지 획득 내역을 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "보상 히스토리 조회 완료",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = RewardHistoryItemDto.class)),
                examples = {
                    @ExampleObject(
                        name = "포인트 히스토리 조회",
                        value = """
                        {
                          "status": 200,
                          "message": "보상 히스토리 조회 완료",
                          "data": [
                            {
                              "date": "2025-01-15",
                              "type": "POINT",
                              "rewardType": "POINT_STORY_READ",
                              "context": "STORY_READ",
                              "value": 30
                            },
                            {
                              "date": "2025-01-15",
                              "type": "POINT",
                              "rewardType": "POINT_WORD_CLICK",
                              "context": "WORD_CLICK",
                              "value": 5
                            },
                            {
                              "date": "2025-01-14",
                              "type": "BADGE",
                              "badgeCode": "BADGE_STORY_1",
                              "badgeName": "첫 번째 동화 읽기"
                            }
                          ]
                        }
                        """
                    )
                }
            )
        ),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (childId 누락, 날짜 형식 오류 등)")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<RewardHistoryItemDto>>> getHistory(
        @Parameter(description = "자녀 ID", example = "1")
        @RequestParam Long childId,
        
        @Parameter(description = "조회 시작일 (YYYY-MM-DD)", example = "2025-01-01")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        
        @Parameter(description = "조회 종료일 (YYYY-MM-DD)", example = "2025-01-31")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
        
        @Parameter(description = "조회 타입 (point: 포인트만, badge: 배지만, 생략: 전체)", example = "point")
        @RequestParam(required = false) String type
    ) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        List<RewardHistoryItemDto> history = rewardHistoryService.getHistory(childId, type, from, to);
        return ResponseEntity.ok(new ApiResponseDto<>(200, "보상 히스토리 조회 완료", history));
    }
} 