package com.storycraft.quiz.controller;

import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.quiz.dto.*;
import com.storycraft.quiz.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/quizzes")
@RequiredArgsConstructor
@Tag(name = "Quiz", description = "퀴즈 관련 API")
public class QuizController {

    private final QuizService quizService;

    @Operation(summary = "퀴즈 생성", description = "GPT 기반으로 퀴즈 여러 개를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "퀴즈가 생성되었습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = QuizCreateResponseDto.class))
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    public ResponseEntity<?> createQuiz(
            @RequestBody @Valid QuizBatchCreateRequestDto dto
    ) {
        return ResponseEntity.status(201).body(
                new ApiResponseDto<>(201, "퀴즈가 생성되었습니다.", quizService.createQuizList(dto.getStoryId(), dto.getQuizList()))
        );
    }

    @Operation(summary = "퀴즈 제출", description = "user가 선택한 정답 제출")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "퀴즈 제출 완료.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = QuizSubmitResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/{quizId}/submit")
    public ResponseEntity<?> submitQuiz(
            @PathVariable Long quizId,
            @RequestBody @Valid QuizSubmitRequestDto dto
    ) {
        return ResponseEntity.status(200).body(
                new ApiResponseDto<>(200, "퀴즈 제출 완료.", quizService.submitQuiz(quizId,dto))
        );
    }

    @Operation(summary = "퀴즈 결과 조회", description = "자녀 ID와 동화 ID 기반으로 푼 퀴즈 결과 및 점수 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "퀴즈 결과 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = QuizResultSummaryResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping("/results")
    public ResponseEntity<?> getQuizResult(
            @RequestParam Long storyId,
            @RequestParam ChildProfile childId
    ) {
        return ResponseEntity.ok(
                new ApiResponseDto<>(200, "퀴즈 결과 조회 성공", quizService.getQuizResultSummary(storyId, childId))
        );
    }

}

