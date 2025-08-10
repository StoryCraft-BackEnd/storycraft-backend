package com.storycraft.quiz.controller;

import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.quiz.dto.*;
import com.storycraft.quiz.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quizzes")
@RequiredArgsConstructor
@Tag(name = "Quiz", description = "퀴즈 관련 API")
public class QuizController {

    private final QuizService quizService;

    @Operation(summary = "동화 기반 퀴즈 자동 생성(10문항)", description = "storyId의 본문에서 중요 단어(**)를 자동 추출하거나, query로 전달한 keywords를 사용해 GPT로 4지선다 10문항을 생성·저장합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "퀴즈 자동 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = QuizCreateResponseDto.class))
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    public ResponseEntity<?> createQuiz(
            @Parameter(description = "동화 ID", example = "1") @RequestParam(name = "storyId") Long storyId,
            @Parameter(description = "키워드들")@RequestParam(name = "keywords",required = false) List<String> keywords
    ) {
        return ResponseEntity.status(201).body(
                new ApiResponseDto<>(201, "퀴즈가 생성되었습니다.", quizService.createQuizList(storyId, keywords))
        );
    }

    @Operation(summary = "퀴즈 리스트 조회", description = "해당 storyId의 모든 퀴즈를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "동화를 찾을 수 없음")
    })
    @GetMapping
    public ResponseEntity<?> getQuizList(
            @RequestParam(name = "storyId") Long storyId
    ) {
        return ResponseEntity.ok(
                new ApiResponseDto<>(200, "퀴즈 리스트 조회 성공", quizService.getQuizList(storyId))
        );
    }

    @Operation(
            summary = "퀴즈 제출",
            description = "주어진 storyId의 모든 문항에 대해 사용자의 선택을 제출합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = QuizSubmitRequestDto.class)),
                            examples = {
                                    @ExampleObject(
                                            name = "퀴즈 10문항 제출 예시",
                                            description = "퀴즈 10문항 제출 예시(동화 ID 맞춰줘야함)",
                                            value = """
                            [
                              { "quizId": 1,  "selectedAnswer": "A" },
                              { "quizId": 2,  "selectedAnswer": "B" },
                              { "quizId": 3,  "selectedAnswer": "C" },
                              { "quizId": 4,  "selectedAnswer": "D" },
                              { "quizId": 5,  "selectedAnswer": "A" },
                              { "quizId": 6,  "selectedAnswer": "B" },
                              { "quizId": 7,  "selectedAnswer": "C" },
                              { "quizId": 8,  "selectedAnswer": "D" },
                              { "quizId": 9,  "selectedAnswer": "A" },
                              { "quizId": 10, "selectedAnswer": "B" }
                            ]
                            """
                                    )
                            }
                    )
            )
    )
    @PostMapping("/submit")
    public ResponseEntity<?> submitQuiz(
            @Parameter(description = "동화 ID", example = "1") @RequestParam(name = "storyId") Long storyId,
            @Parameter(description = "자녀 ID", example = "1") @RequestParam(name = "childId") Long childId,
            @RequestBody @Valid List<QuizSubmitRequestDto> answers
    ) {
        return ResponseEntity.status(200).body(
                new ApiResponseDto<>(200, "퀴즈 제출 완료.", quizService.submitQuiz(storyId, childId, answers))
        );
    }

    @Operation(summary = "퀴즈 결과 조회", description = "자녀 ID와 동화 ID로 최근 제출 결과(총점/정답 수 등)를 조회합니다."
    )
    @ApiResponses({
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
            @Parameter(description = "동화 ID", example = "1") @RequestParam(name = "storyId") Long storyId,
            @Parameter(description = "자녀 ID", example = "1") @RequestParam(name = "childId") Long childId
    ) {
        return ResponseEntity.ok(
                new ApiResponseDto<>(200, "퀴즈 결과 조회 성공", quizService.getQuizResultSummary(storyId, childId))
        );
    }

}

