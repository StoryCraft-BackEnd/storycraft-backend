package com.storycraft.ai.controller;

import com.storycraft.ai.dto.AiDalleRequestDto;
import com.storycraft.ai.dto.AiDalleResponseDto;
import com.storycraft.ai.service.AiDalleService;
import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.global.util.S3Uploader;
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

@RestController
@RequestMapping("/ai/dalle")
@RequiredArgsConstructor
@Tag(name = "AI", description = "삽화 생성 AI 관련 API")
public class AiDalleController {

    private final AiDalleService aiDalleService;
    private final S3Uploader s3Uploader;

    @Operation(summary = "DALLE 이미지 생성", description = "prompt를 기반으로 이미지를 생성하고, S3에 저장한 URL을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "이미지 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AiDalleResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/generate")
    public ApiResponseDto<AiDalleResponseDto> generateImage(
            @RequestBody AiDalleRequestDto request
    ) {
        byte[] imageBytes = aiDalleService.generateImage(request.getPrompt());

        String imageUrl = s3Uploader.uploadBytes(imageBytes, "illustrations", "prompt.png");
        return new ApiResponseDto<>(200, "이미지 생성 성공",  new AiDalleResponseDto(imageUrl));
    }
}
