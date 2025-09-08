package com.storycraft.integration.controller;

import com.storycraft.auth.service.UserDetailsImpl;
import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.global.security.OwnershipGuard;
import com.storycraft.illustration.service.IllustrationService;
import com.storycraft.integration.service.IntegrationService;
import com.storycraft.integration.dto.StoryIntegrationDto;
import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.speech.service.SpeechService;
import com.storycraft.story.dto.StoryRequestDto;
import com.storycraft.story.dto.StoryResponseDto;
import com.storycraft.story.entity.Story;
import com.storycraft.story.service.StoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/integration/stories")
@Tag(name = "Integration", description = "통합 관련 API")
public class StoryIntegrationController {

    private final StoryService storyService;
    private final IllustrationService illustrationService;
    private final SpeechService speechService;
    private final IntegrationService integrationService;
    private final OwnershipGuard ownershipGuard;

    @Operation(summary = "동화 생성 및 단어/퀴즈 일괄 생성", description = "StoryService로 동화를 생성하고, 단어 추출/퀴즈 생성까지 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "동화 생성 및 단어/퀴즈 일괄 생성에 성공했습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StoryResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })

    @PostMapping
    public ResponseEntity<ApiResponseDto<StoryResponseDto>> createStoryAndWordsAndQuizzes(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "자녀 프로필 ID", example = "1") @RequestParam(name = "childId") Long childId,
            @RequestBody @Valid StoryRequestDto dto
    ) {
        Long userId = userDetails.getUser().getId();
        ChildProfile child = ownershipGuard.getOwnedChildOrThrow(childId, userId);
        return ResponseEntity.status(201).body(
                new ApiResponseDto<>(201, "동화 생성 및 자원 생성 완료", integrationService.createStoryAndWordsAndQuizzes(child, dto))
        );
    }

    @Operation(summary = "동화 통합 조회", description = "동화 ID 기반으로 본문, 삽화, TTS 음성을 함께 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StoryIntegrationDto.class)
                    )),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "해당 동화를 찾을 수 없습니다.")
    })

    @GetMapping("/{storyId}")
    public ApiResponseDto<StoryIntegrationDto> getStoryIntegration(
            @PathVariable Long storyId
    ) {
        Story story = storyService.getStoryEntityById(storyId);
        String imageUrl = illustrationService.getUrlByStoryId(storyId);     // 삽화 이미지 S3 URL
        String ttsUrl = speechService.getTtsUrlByStoryId(storyId);          // TTS MP3 S3 URL

        StoryIntegrationDto dto = new StoryIntegrationDto(
                story.getId(),
                story.getTitle(),
                story.getContent(),
                imageUrl,
                ttsUrl,
                story.getCreatedAt()
        );

        return new ApiResponseDto<>(200, "동화 통합 조회에 성공했습니다.", dto);
    }
}
