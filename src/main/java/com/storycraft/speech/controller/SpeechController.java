package com.storycraft.speech.controller;

import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.speech.dto.SttResponseDto;
import com.storycraft.speech.dto.TtsCreateRequestDto;
import com.storycraft.speech.dto.TtsCreateResponseDto;
import com.storycraft.speech.service.SpeechService;
import com.storycraft.story.dto.StoryResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/speech")
@RequiredArgsConstructor
@Tag(name = "Speech", description = "음성 관련 API")
public class SpeechController {

    private final SpeechService speechService;
    private final OwnershipGuard ownershipGuard;

    @Operation(summary = "TTS 생성", description = "동화 단락ID, 성우, 속도 정보를 기반으로 Polly TTS 음성을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "TTS가 성공적으로 생성되었습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TtsCreateResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/tts")
    public ResponseEntity<?> createTts(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "자녀 프로필 ID", example = "1") @RequestParam(name = "child_id") Long childId,
            @Parameter(description = "동화 단락 ID", example = "1") @RequestParam(name = "section_id") Long sectionId,
            @Parameter(description = "동화 ID", example = "1") @RequestParam(name = "story_id",required = false) Long storyId,
            @Parameter(description = "성우 ID", example = "Joanna") @RequestParam(name = "voice_id") VoiceId voiceId,
            @Parameter(description = "재생속도", example = "0.8") @RequestParam(name = "speech_rate", required = false, defaultValue = "0.8") Float speechRate
    ) {
        try {
            TtsCreateResponseDto responseDto = speechService.createTts(dto);
            return ResponseEntity.status(201).body(
                    new ApiResponseDto<>(201, "TTS가 성공적으로 생성되었습니다.", responseDto)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponseDto<>(400, "잘못된 요청: " + e.getMessage(), null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    new ApiResponseDto<>(500, "서버 오류: " + e.getMessage(), null)
            );
        }
    }

    @Operation(summary = "STT 변환", description = "사용자의 음성 파일을 텍스트로 변환하고 키워드를 추출합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "STT 변환 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SttResponseDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping(value = "/stt", consumes = "multipart/form-data")
    public ResponseEntity<?> transcribeStt(
            @RequestPart(name = "audioFile") MultipartFile audioFile
    ) {
        List<String> keywords = speechService.transcribeStt(audioFile).getKeywords();

        return ResponseEntity.ok(
                new ApiResponseDto<>(200, "STT 변환 성공", SttResponseDto.builder()
                        .keywords(keywords)
                        .build())
        );
    }

/*    @Operation(summary = "STT 기반 동화 생성", description = "음성 파일을 업로드하여 STT로 키워드를 추출하고, 이를 기반으로 GPT가 동화를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "동화 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StoryResponseDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping(value = "/generate-from-stt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)

    public ApiResponseDto<StoryResponseDto> generateStoryFromStt(
            @RequestPart(name = "file") MultipartFile file,
            @RequestParam(name = "childId") ChildProfile childId
    ) {
        StoryResponseDto response = speechService.generateStoryFromStt(file, childId);
        return new ApiResponseDto<>(200, "동화 생성 성공", response);
    }*/
}
