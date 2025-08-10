package com.storycraft.dictionary.controller;

import com.storycraft.dictionary.dto.SaveWordResponseDto;
import com.storycraft.dictionary.dto.WordResponseDto;
import com.storycraft.dictionary.service.DictionaryService;
import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.profile.entity.ChildProfile;
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
            @Parameter(description = "하이라이트된 단어") @RequestParam(name = "word") String word
    ) {
        return ResponseEntity.ok(
                new ApiResponseDto<>(200, "단어 조회에 성공했습니다", dictionaryService.getWord(word))
        );
    }

    @Operation(summary = "동화 ID로 단어 추출 및 자녀에게 단어 저장", description = "동화 본문에서 **로 감싼 단어들을 추출하고, 단어 정보를 GPT로 조회하여 DB에 저장 후 자녀에게 연동합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "단어 저장 성공",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = SaveWordResponseDto.class))
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/words/save-by-story")
    public ResponseEntity<?> extractAndSaveWordsByStoryId(
            @Parameter(description = "동화 ID", example = "1")
            @RequestParam(name = "storyId") Long storyId,

            @Parameter(description = "자녀 프로필 ID", example = "3")
            @RequestParam(name = "childId") Long childId
    ) {
        List<SaveWordResponseDto> savedWords = dictionaryService.extractWordsAndSave(storyId, childId);
        return ResponseEntity.ok(
                new ApiResponseDto<>(200, "단어 저장에 성공했습니다", savedWords)
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
            @Parameter(description = "유저 ID") @RequestParam(name = "userID") Long userId,
            @Parameter(description = "자녀 프로필 ID") @RequestParam(name = "childID") Long childId,
            @Parameter(description = "하이라이트된 단어") @RequestParam(name = "word") String word
    ) {
        return ResponseEntity.status(201).body(
                new ApiResponseDto<>(201, "단어 저장에 성공했습니다.", dictionaryService.savedWord(userId, childId, word))
        );
    }


    @Operation(summary = "자녀 단어 목록 조회", description = "특정 자녀가 저장한 모든 단어를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "단어 목록 조회에 성공했습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SaveWordResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "자녀 정보를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "403", description = "해당 자녀에 대한 접근 권한이 없습니다.")
    })
    @GetMapping("/words/list")
    public ResponseEntity<?> getSavedWords(
            @Parameter(description = "로그인한 사용자 ID") @RequestParam(name = "userID") Long userId,
            @Parameter(description = "자녀 프로필 ID") @RequestParam(name = "childId") Long childId
    ) {
        return ResponseEntity.ok(
                new ApiResponseDto<>(200, "단어 목록 조회에 성공했습니다.", dictionaryService.getSavedWords(userId, childId))
        );
    }


    @Operation(summary = "단어 삭제", description = "자녀가 저장한 단어를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "단어 삭제에 성공했습니다."),
            @ApiResponse(responseCode = "404", description = "단어를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "403", description = "해당 단어에 대한 삭제 권한이 없습니다.")
    })
    @DeleteMapping("/words/{savedId}")
    public ResponseEntity<?> deleteSavedWord(
            @Parameter(description = "로그인한 사용자 ID") @RequestParam(name = "userID") Long userId,
            @Parameter(description = "삭제할 단어 ID",example = "1") @PathVariable(name = "savedId") Long savedId
    ) {
        dictionaryService.deleteSavedWord(userId, savedId);
        return ResponseEntity.ok(
                new ApiResponseDto<>(200, "단어 삭제에 성공했습니다.", null)
        );
    }
}
