package com.storycraft.ai.controller;

import com.storycraft.ai.dto.AiWhisperResponseDto;
import com.storycraft.ai.service.AiWhisperService;
import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.speech.dto.SttResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/ai/whisper")
@RequiredArgsConstructor
@Tag(name = "Whisper", description = "Whisper 관련 API")
public class AiWhisperController {

    private final AiWhisperService aiWhisperService;

    @Operation(summary = "Whisper 음성 텍스트 변환", description = "음성 파일을 텍스트로 변환합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "변환 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SttResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping(value = "/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponseDto<AiWhisperResponseDto> transcribe(
            @RequestPart MultipartFile file
    ) throws Exception {
        String result = aiWhisperService.transcribeAudio(file);
        return new ApiResponseDto<>(200,"변환 성공",new AiWhisperResponseDto(result));
    }
}
