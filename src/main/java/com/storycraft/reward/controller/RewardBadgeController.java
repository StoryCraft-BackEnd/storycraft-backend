package com.storycraft.reward.controller;

import com.storycraft.auth.jwt.SecurityUtil;
import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.profile.repository.ChildProfileRepository;
import com.storycraft.reward.dto.BadgeInfoDto;
import com.storycraft.reward.service.RewardBadgeService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/rewards/badges")
@RequiredArgsConstructor
@Tag(name = "Reward Badges", description = "배지 관련 API")
public class RewardBadgeController {
    private final RewardBadgeService rewardBadgeService;
    private final ChildProfileRepository childProfileRepository;

    @Operation(
        summary = "사용 가능한 배지 목록 조회", 
        description = "시스템에서 제공하는 모든 배지의 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "사용 가능한 배지 목록 조회 완료",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = BadgeInfoDto.class)),
                examples = {
                    @ExampleObject(
                        name = "배지 목록 조회",
                        value = """
                        {
                          "status": 200,
                          "message": "사용 가능한 배지 목록 조회 완료",
                          "data": [
                            {
                              "badgeCode": "BADGE_STORY_1",
                              "badgeName": "첫 번째 동화 읽기",
                              "condition": "동화 1편 읽기",
                              "category": "BASIC_LEARNING"
                            },
                            {
                              "badgeCode": "BADGE_STREAK_7",
                              "badgeName": "7일 연속 학습",
                              "condition": "7일 연속 학습",
                              "category": "STREAK"
                            },
                            {
                              "badgeCode": "BADGE_LEVEL_10",
                              "badgeName": "레벨 10 달성!",
                              "condition": "누적 포인트 기준 레벨 10 도달",
                              "category": "MILESTONE"
                            }
                          ]
                        }
                        """
                    )
                }
            )
        )
    })
    @GetMapping("/available")
    public ResponseEntity<ApiResponseDto<List<BadgeInfoDto>>> getAllAvailableBadges() {
        List<BadgeInfoDto> badges = RewardBadgeService.BadgeDefinition.getAllBadges().stream()
                .map(badge -> BadgeInfoDto.builder()
                        .badgeCode(badge.getBadgeCode())
                        .badgeName(badge.getBadgeName())
                        .condition(badge.getCondition())
                        .category(getBadgeCategory(badge.getBadgeCode()))
                        .build())
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(new ApiResponseDto<>(200, "사용 가능한 배지 목록 조회 완료", badges));
    }

    @Operation(
        summary = "자녀 배지 목록 조회", 
        description = "특정 자녀가 획득한 배지 목록을 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "자녀 배지 목록 조회 완료",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = BadgeInfoDto.class)),
                examples = {
                    @ExampleObject(
                        name = "자녀 배지 목록",
                        value = """
                        {
                          "status": 200,
                          "message": "자녀 배지 목록 조회 완료",
                          "data": [
                            {
                              "badgeCode": "BADGE_STORY_1",
                              "badgeName": "첫 번째 동화 읽기",
                              "condition": "",
                              "category": "BASIC_LEARNING"
                            },
                            {
                              "badgeCode": "BADGE_WORD_100",
                              "badgeName": "단어 수집가",
                              "condition": "",
                              "category": "MILESTONE"
                            }
                          ]
                        }
                        """
                    )
                }
            )
        ),
        @ApiResponse(responseCode = "400", description = "존재하지 않는 자녀입니다.")
    })
    @GetMapping("/child/{childId}")
    public ResponseEntity<ApiResponseDto<List<BadgeInfoDto>>> getChildBadges(
        @Parameter(description = "자녀 ID", example = "1")
        @PathVariable Long childId
    ) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        
        ChildProfile child = childProfileRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자녀입니다."));
        
        List<BadgeInfoDto> badges = rewardBadgeService.getChildBadges(child).stream()
                .map(badge -> BadgeInfoDto.builder()
                        .badgeCode(badge.getBadgeCode())
                        .badgeName(badge.getBadgeName())
                        .condition("") // 이미 획득한 배지는 조건 표시 불필요
                        .category(getBadgeCategory(badge.getBadgeCode()))
                        .build())
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(new ApiResponseDto<>(200, "자녀 배지 목록 조회 완료", badges));
    }

    private String getBadgeCategory(String badgeCode) {
        if (RewardBadgeService.BadgeDefinition.BASIC_LEARNING_BADGES.stream()
                .anyMatch(badge -> badge.getBadgeCode().equals(badgeCode))) {
            return "BASIC_LEARNING";
        } else if (RewardBadgeService.BadgeDefinition.MILESTONE_BADGES.stream()
                .anyMatch(badge -> badge.getBadgeCode().equals(badgeCode))) {
            return "MILESTONE";
        } else if (RewardBadgeService.BadgeDefinition.STREAK_BADGES.stream()
                .anyMatch(badge -> badge.getBadgeCode().equals(badgeCode))) {
            return "STREAK";
        } else if (RewardBadgeService.BadgeDefinition.SPECIAL_CHALLENGE_BADGES.stream()
                .anyMatch(badge -> badge.getBadgeCode().equals(badgeCode))) {
            return "SPECIAL_CHALLENGE";
        }
        return "UNKNOWN";
    }
} 