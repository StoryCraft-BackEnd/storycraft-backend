package com.storycraft.story.controller;

import com.storycraft.auth.service.UserDetailsImpl;
import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.story.dto.*;
import com.storycraft.story.entity.StoryProgress;
import com.storycraft.story.service.StoryProgressService;
import com.storycraft.story.service.StorySectionService;
import com.storycraft.story.service.StoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.storycraft.global.security.OwnershipGuard;

import java.util.Optional;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/stories")
@RequiredArgsConstructor
@Tag(name = "Story", description = "동화 관련 API")
public class StoryController {

    private final StoryService storyService;
    private final StorySectionService storySectionService;
    private final StoryProgressService storyProgressService;
    private final OwnershipGuard ownershipGuard;

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
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "자녀 프로필 ID", example = "1") @RequestParam(name = "childId") Long childId,
            @RequestBody @Valid StoryRequestDto dto
    ) {
        Long userId = userDetails.getUser().getId();
        ChildProfile child = ownershipGuard.getOwnedChildOrThrow(childId, userId);
        return ResponseEntity.status(201).body(
                new ApiResponseDto<>(201, "동화 생성에 성공했습니다.", storyService.createStory(child, dto))
        );
    }

    @Operation(summary = "동화 단락 조회", description = "storyId에 해당하는 동화 단락들을 순서대로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "단락 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = StorySectionDto.class))
                    )
            ),
            @ApiResponse(responseCode = "404", description = "해당 동화가 존재하지 않습니다.")
    })
    @GetMapping("/{id}/sections")
    public ResponseEntity<?> getStorySections(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "동화 ID", example = "1") @PathVariable(name = "storyId") Long storyId,
            @Parameter(description = "자녀 프로필 ID", example = "1") @RequestParam(name = "childId") Long childId
    ) {
        Long userId = userDetails.getUser().getId();
        ChildProfile child = ownershipGuard.getOwnedChildOrThrow(childId, userId);
        return ResponseEntity.ok(
                new ApiResponseDto<>(200, "단락 조회에 성공했습니다.", storySectionService.getSectionsByStoryId(storyId))
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
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "조회할 동화 ID", example = "1") @PathVariable(name = "storyId") Long storyId,
            @Parameter(description = "자녀 프로필 ID", example = "1") @RequestParam(name = "childId") Long childId
    ) {
        Long userId = userDetails.getUser().getId();
        ChildProfile child = ownershipGuard.getOwnedChildOrThrow(childId, userId);

        StoryResponseDto storyDto = storyService.getStory(storyId, child);
        Optional<StoryProgress> progressOpt = storyProgressService.findByStoryIdAndChildId(storyId, childId);

        if (progressOpt.isPresent()) {
            storyDto.setProgress(StoryProgressResponseDto.fromEntity(progressOpt.get()));
        }

        return ResponseEntity.ok(
                new ApiResponseDto<>(200, "동화 조회에 성공했습니다.", storyDto)
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
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "자녀 프로필 ID", example = "1") @RequestParam(name = "childId") Long childId
    ) {
        Long userId = userDetails.getUser().getId();
        ChildProfile child = ownershipGuard.getOwnedChildOrThrow(childId, userId);

        return ResponseEntity.ok(
                new ApiResponseDto<>(200, "동화 목록 조회에 성공했습니다.", storyService.getStoryList(child))
        );
    }

    @Operation(summary = "동화 수정", description = "키워드를 다시 받아 동화 제목 및 내용을 수정합니다.")
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
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "수정할 동화 ID", example = "1") @PathVariable(name = "storyId") Long storyId,
            @Parameter(description = "자녀 프로필 ID", example = "1") @RequestParam(name = "childId") Long childId,
            @RequestBody StoryUpdateDto dto
    ) {
        Long userId = userDetails.getUser().getId();
        ChildProfile child = ownershipGuard.getOwnedChildOrThrow(childId, userId);

        return ResponseEntity.ok(
                new ApiResponseDto<>(200, "동화 수정에 성공했습니다.", storyService.updateStory(id, child, dto))
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
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "삭제할 동화 ID", example = "1") @PathVariable(name = "id") Long id,
            @Parameter(description = "자녀 프로필 ID", example = "1") @RequestParam(name = "childId") Long childId
    ) {
        Long userId = userDetails.getUser().getId();
        ChildProfile child = ownershipGuard.getOwnedChildOrThrow(childId, userId);

        storyService.deleteStory(id, child);
        return ResponseEntity.ok(
                new ApiResponseDto<>(200, "동화가 성공적으로 삭제되었습니다.", null)
        );
    }
}

