package com.storycraft.ai.controller;

import com.storycraft.ai.dto.AiStoryRequestDto;
import com.storycraft.ai.dto.StoryContentDto;
import com.storycraft.ai.service.AiGptService;
import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.profile.repository.ChildProfileRepository;
import com.storycraft.story.dto.StoryResponseDto;
import com.storycraft.story.entity.Story;
import com.storycraft.story.repository.StoryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/ai/gpt")
@RequiredArgsConstructor
@Tag(name = "AI", description = "동화 생성 관련 API")
public class AiStoryController {
    private final AiGptService aiGptService;
    private final StoryRepository storyRepository;
    private final ChildProfileRepository childProfileRepository;

    @Operation(summary = "GPT 동화 생성", description = "키워드를 기반으로 GPT가 유아용 동화를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "동화 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StoryResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/generate")
    public ApiResponseDto<StoryResponseDto> generateStory(
            @RequestBody AiStoryRequestDto request
            ) {
        ChildProfile child = childProfileRepository.findById(request.getChildId())
                .orElseThrow(() -> new IllegalArgumentException("자녀 정보를 찾을 수 없습니다."));

        String level = String.valueOf(child.getLearningLevel());

        StoryContentDto result = aiGptService.generateStoryContent(Collections.singletonList(request.getKeyword()), level);

        Story story = Story.builder()
                .title(result.getTitle())
                .content(result.getContent())
                .childId(child)
                .build();

        Story saved = storyRepository.save(story);
        return new ApiResponseDto<>(200, "동화 생성 성공", StoryResponseDto.fromEntity(saved));
    }
}
