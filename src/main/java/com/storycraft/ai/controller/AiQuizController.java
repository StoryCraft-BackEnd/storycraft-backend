package com.storycraft.ai.controller;

import com.storycraft.ai.dto.AiQuizRequestDto;
import com.storycraft.ai.dto.AiQuizResponseDto;
import com.storycraft.ai.service.AiQuizService;
import com.storycraft.global.response.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("ai/gpt/quiz")
@RequiredArgsConstructor
@Tag(name = "AI", description = "퀴즈 생성 관련 API")
public class AiQuizController {

    private final AiQuizService aiQuizService;

    @Operation(summary = "GPT 기반 퀴즈 생성", description = "저장된 동화 ID와 하이라이트된 단어들을 기반으로 퀴즈를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "퀴즈 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AiQuizResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "해당 동화를 찾을 수 없습니다.")
    })
    @PostMapping
    public ResponseEntity<ApiResponseDto<List<AiQuizResponseDto>>> generateQuiz(
            @RequestBody @Valid AiQuizRequestDto requestDto
    ) {
        List<AiQuizResponseDto> result = aiQuizService.generateQuizFromStory(requestDto);
        return ResponseEntity.status(200).body(
                new ApiResponseDto<>(200, "퀴즈 생성 완료", result)
        );
    }
}
