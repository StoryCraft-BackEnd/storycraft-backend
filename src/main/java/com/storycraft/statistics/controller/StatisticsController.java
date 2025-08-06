package com.storycraft.statistics.controller;

import com.storycraft.auth.jwt.SecurityUtil;
import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.statistics.dto.ChildStatisticsResponseDto;
import com.storycraft.statistics.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
@Tag(name = "Statistics", description = "학습 통계 관련 API")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Operation(summary = "자녀별 학습 통계 조회", description = "특정 자녀의 학습 통계 데이터를를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "학습 통계 조회에 성공했습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ChildStatisticsResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "자녀 프로필을 찾을 수 없습니다."),
            @ApiResponse(responseCode = "403", description = "해당 자녀에 대한 접근 권한이 없습니다.")
    })
    @GetMapping("/children/{childId}")
    public ResponseEntity<ApiResponseDto<ChildStatisticsResponseDto>> getChildStatistics(
            @Parameter(description = "자녀 프로필 ID", example = "1") 
            @PathVariable Long childId
    ) {
        String email = SecurityUtil.getCurrentUserEmail();
        ChildStatisticsResponseDto response = statisticsService.getChildStatistics(email, childId);

        return ResponseEntity.ok(
                new ApiResponseDto<>(200, "학습 통계 조회에 성공했습니다.", response)
        );
    }
} 