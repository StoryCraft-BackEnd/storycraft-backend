package com.storycraft.recommendation.controller;

import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.recommendation.dto.RecommendResponseDto;
import com.storycraft.recommendation.dto.RecommendationFeedbackRequestDto;
import com.storycraft.recommendation.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
@Tag(name = "Recommendation", description = "동화 추천 및 피드백 API")
public class RecommendationController {

    private final RecommendationService recommendationService;

    /**
     * 자녀 ID를 기준으로 추천 동화 목록을 조회
     */
    @Operation(summary = "동화 추천", description = "자녀에게 맞는 동화 추천 리스트를 제공합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "추천 동화 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RecommendResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping
    public ApiResponseDto<?> getRecommendations(@RequestParam ChildProfile childId) {
        List<RecommendResponseDto> recommendations = recommendationService.getRecommendations(childId);
        return new ApiResponseDto<>(200, "동화 추천 결과", recommendations);
    }

    /**
     * 자녀의 추천 결과 피드백 저장
     */
    @Operation(summary = "추천 피드백", description = "추천 동화에 대한 사용자 피드백 저장")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "피드백 저장 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "해당 동화를 찾을 수 없습니다.")
    })
    @PostMapping("/feedback")
    public ApiResponseDto<?> saveFeedback(@RequestBody RecommendationFeedbackRequestDto dto) {
        recommendationService.saveOrUpdateFeedback(dto);
        return new ApiResponseDto<>(200, "피드백이 저장되었습니다.", null);
    }
}
