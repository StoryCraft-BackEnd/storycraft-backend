package com.storycraft.dictionary.controller;

import com.storycraft.dictionary.dto.SaveWordResponseDto;
import com.storycraft.dictionary.dto.WordResponseDto;
import com.storycraft.dictionary.service.DictionaryService;
import com.storycraft.global.response.ApiResponseDto;
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
@RequestMapping("/dictionaries")
@RequiredArgsConstructor
@Tag(name = "Dictionary", description = "단어 관련 API")
public class DictionaryController {

    private final DictionaryService dictionaryService;

    @Operation(summary = "단어 조회", description = "영어 단어의 뜻과 예문을 반환 합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "단어 조회에 성공했습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WordResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping("/words")
    public ResponseEntity<?> getWord(
            @Parameter(description = "하이라이트된 단어")@RequestParam String word
    ) {
        return ResponseEntity.ok(
                new ApiResponseDto<>(200, "단어 조회에 성공했습니다", dictionaryService.getWord(word))
        );
    }


    @Operation(summary = "단어 저장", description = "영어 단어를 사용자 사전에 저장합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "단어 저장에 성공했습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SaveWordResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "단어를 찾을 수 없습니다.")
    })
    @PostMapping("/words/save")
    public ResponseEntity<?> saveWord(
            @Parameter(description = "자녀 프로필 ID") @RequestParam ChildProfile childId,
            @Parameter(description = "하이라이트된 단어") @RequestParam String word
    ) {
        return ResponseEntity.status(201).body(
                new ApiResponseDto<>(201, "단어 저장에 성공했습니다.", dictionaryService.savedWord(childId, word))
        );
    }
}
