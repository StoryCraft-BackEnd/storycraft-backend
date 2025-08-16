package com.storycraft.statistics.controller;

import com.storycraft.auth.jwt.SecurityUtil;
import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.statistics.dto.ChildStatisticsResponseDto;
import com.storycraft.statistics.dto.TotalLearningTimeRequestDto;
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

    @Operation(
        summary = "자녀별 학습 통계 조회", 
        description = "특정 자녀의 학습 통계 데이터를 조회합니다. " +
                     "생성한 동화 수, 완료한 동화 수, 학습한 단어 개수, 푼 퀴즈 수, 총 학습 시간을 반환합니다. " +
                     "현재 로그인한 사용자의 자녀만 조회 가능합니다."
    )
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
            @ApiResponse(responseCode = "403", description = "해당 자녀에 대한 접근 권한이 없습니다."),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰입니다.")
    })
    @GetMapping("/children/{childId}")
    public ResponseEntity<ApiResponseDto<ChildStatisticsResponseDto>> getChildStatistics(
            @Parameter(
                description = "자녀 프로필 ID", 
                example = "1",
                required = true
            ) 
            @PathVariable Long childId
    ) {
        String email = SecurityUtil.getCurrentUserEmail();
        ChildStatisticsResponseDto response = statisticsService.getChildStatistics(email, childId);

        return ResponseEntity.ok(
                new ApiResponseDto<>(200, "학습 통계 조회에 성공했습니다.", response)
        );
    }

    @Operation(
        summary = "총 학습 시간 저장", 
        description = "프론트엔드로부터 받은 총 학습 시간을 저장합니다. " +
                     "기존 기록이 있으면 업데이트하고, 없으면 새로 생성합니다. " +
                     "현재 로그인한 사용자의 자녀만 저장 가능합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "총 학습 시간 저장에 성공했습니다."
            ),
            @ApiResponse(responseCode = "404", description = "자녀 프로필을 찾을 수 없습니다."),
            @ApiResponse(responseCode = "403", description = "해당 자녀에 대한 접근 권한이 없습니다."),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰입니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터입니다.")
    })
    @PostMapping("/learning-time")
    public ResponseEntity<ApiResponseDto<Void>> saveTotalLearningTime(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "총 학습 시간 저장 요청 데이터",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TotalLearningTimeRequestDto.class)
                )
            )
            @RequestBody TotalLearningTimeRequestDto requestDto
    ) {
        String email = SecurityUtil.getCurrentUserEmail();
        statisticsService.saveTotalLearningTime(email, requestDto);

        return ResponseEntity.ok(
                new ApiResponseDto<>(200, "총 학습 시간 저장에 성공했습니다.", null)
        );
    }
} 