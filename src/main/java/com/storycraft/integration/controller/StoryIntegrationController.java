package com.storycraft.integration.controller;

import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.illustration.service.IllustrationService;
import com.storycraft.integration.dto.StoryIntegrationDto;
import com.storycraft.speech.service.SpeechService;
import com.storycraft.story.entity.Story;
import com.storycraft.story.service.StoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/integration/stories")
@Tag(name = "Story Integration", description = "동화 전체 데이터 통합 조회 API")
public class StoryIntegrationController {

    private final StoryService storyService;
    private final IllustrationService illustrationService;
    private final SpeechService speechService;


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
