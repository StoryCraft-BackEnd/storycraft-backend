package com.storycraft.story.controller;

import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.story.dto.StoryRequestDto;
import com.storycraft.story.dto.StoryUpdateDto;
import com.storycraft.story.service.StoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/stories")
@RequiredArgsConstructor
@Tag(name = "Story", description = "동화 관련 API")
public class StoryController {

    private final StoryService storyService;

    @Operation(summary = "동화 생성", description = "prompt로 AI 기반 동화를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "동화 생성에 성공했습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StoryResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    public ResponseEntity<?> createStory(
            @RequestBody @Valid StoryRequestDto dto
    ) {
        return ResponseEntity.status(201).body(
                new ApiResponseDto<>(201, "동화 생성에 성공했습니다.", storyService.createStory(dto))
        );
    }

    @Operation(summary = "동화 상세 조회", description = "storyId로 특정 동화를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "동화 조회에 성공했습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StoryResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "동화를 찾을 수 없습니다.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getStory(
            @Parameter(description = "조회할 동화 ID", example = "1")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                new ApiResponseDto<>(200, "동화 조회에 성공했습니다.", storyService.getStory(id))
        );
    }

    @Operation(summary = "동화 목록 조회", description = "자녀 ID 기준 동화 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "동화 목록 조회에 성공했습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = StoryResponseDto.class))
                    )
            )
    })
    @GetMapping("/lists")
    public ResponseEntity<?> getList(
            @Parameter(description = "자녀 ID", example = "child-uuid-1234")
            @RequestParam String childId
    ) {
        return ResponseEntity.ok(
                new ApiResponseDto<>(200, "동화 목록 조회에 성공했습니다.", storyService.getStoryList(childId))
        );
    }

    @Operation(summary = "동화 수정", description = "동화 제목 및 내용을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "동화 수정에 성공했습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StoryResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "수정할 동화를 찾을 수 없습니다.")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateStory(
            @Parameter(description = "수정할 동화 ID", example = "1")
            @PathVariable Long id,
            @RequestBody StoryUpdateDto dto
    ) {
        return ResponseEntity.ok(
                new ApiResponseDto<>(200, "동화 수정에 성공했습니다.", storyService.updateStory(id, dto))
        );
    }

    @Operation(summary = "동화 삭제", description = "동화 ID로 해당 동화를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "동화 삭제에 성공했습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "삭제할 동화를 찾을 수 없습니다.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStory(
            @Parameter(description = "삭제할 동화 ID", example = "1")
            @PathVariable Long id
    ) {
        storyService.deleteStory(id);
        return ResponseEntity.ok(
                new ApiResponseDto<>(200, "동화가 성공적으로 삭제되었습니다.", null)
        );
    }
}

