package com.storycraft.illustration.controller;

import com.storycraft.auth.service.UserDetailsImpl;
import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.global.security.OwnershipGuard;
import com.storycraft.illustration.dto.IllustrationResponseDto;
import com.storycraft.illustration.dto.SectionIllustrationRequestDto;
import com.storycraft.illustration.dto.SectionIllustrationResponseDto;
import com.storycraft.illustration.service.IllustrationService;
import com.storycraft.profile.entity.ChildProfile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
@RequestMapping("/illustrations")
@RequiredArgsConstructor
@Tag(name = "Illustration", description = "삽화 관련 API")
public class IllustrationController {

    private final IllustrationService illustrationService;
    private final OwnershipGuard ownershipGuard;


    @Operation(summary = "동화 단락별 삽화 생성(3개씩)", description = "storyId를 기반으로 해당 동화의 각 단락 내용으로부터 삽화를 3개씩 자동 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "단락별 삽화 생성에 성공했습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SectionIllustrationResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 형식이 잘못되었습니다."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 storyId입니다."
            )
    })
    @PostMapping("/sections")
    public ResponseEntity<ApiResponseDto<SectionIllustrationResponseDto>> createIllustrationsByStory(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(name = "storyId") Long storyId,
            @RequestParam(name = "childId") Long childId
    ) {
        Long userId= userDetails.getUser().getId();
        ChildProfile child = ownershipGuard.getOwnedChildOrThrow(childId, userId);

        return ResponseEntity.status(201).body(
                new ApiResponseDto<>(201,"단락별 삽화 생성에 성공했습니다.",illustrationService.createSectionIllustrations(storyId, child))
        );
    }

    @Operation(summary = "동화 모든 단락별 삽화 생성", description = "storyId를 기반으로 해당 동화의 모든 단락 내용으로부터 삽화를 자동 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "단락별 삽화 생성에 성공했습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SectionIllustrationResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 형식이 잘못되었습니다."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 storyId입니다."
            )
    })
    @PostMapping("/sections/all")
    public ResponseEntity<ApiResponseDto<SectionIllustrationResponseDto>> createAllIllustrationsByStory(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(name = "childId") Long childId,
            @RequestBody @Valid SectionIllustrationRequestDto requestDto
    ) {
        Long userId= userDetails.getUser().getId();
        ChildProfile child = ownershipGuard.getOwnedChildOrThrow(childId, userId);

        return ResponseEntity.status(201).body(
                new ApiResponseDto<>(201,"단락별 삽화 생성에 성공했습니다.",illustrationService.createAllSectionIllustrations(requestDto.getStoryId(), child))
        );
    }

    @Operation(summary = "삽화 상세 조회", description = "삽화 ID로 특정 삽화를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "삽화 조회에 성공했습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = IllustrationResponseDto.class)
                    )

            ),
            @ApiResponse(responseCode = "404", description = "삽화를 찾을 수 없습니다.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getIllustration(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "조회할 삽화 ID", example = "1") @PathVariable(name = "id") Long id,
            @Parameter(description = "자녀 프로필 ID", example = "1") @RequestParam(name = "childId") Long childId
    ) {
        Long userId = userDetails.getUser().getId();
        ChildProfile child = ownershipGuard.getOwnedChildOrThrow(childId, userId);
        return ResponseEntity.ok(
                new ApiResponseDto<>(200, "삽화 조회에 성공했습니다.", illustrationService.getIllustration(id, child))
        );
    }

    @Operation(summary = "삽화 목록 조회", description = "삽화 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "삽화 목록 조회에 성공했습니다",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = IllustrationResponseDto.class))
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping
    public ResponseEntity<?> getList(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "자녀 프로필 ID", example = "1") @RequestParam(name = "childId") Long childId
    ) {
        Long userId = userDetails.getUser().getId();
        ChildProfile child = ownershipGuard.getOwnedChildOrThrow(childId, userId);

        return ResponseEntity.ok(
                new ApiResponseDto<>(200, "삽화 목록 조회에 성공했습니다.", illustrationService.getIllustraitonList(child))
        );
    }

    @Operation(summary = "삽화 삭제", description = "삽화 ID로 삽화를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "삽화 삭제에 성공했습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "삭제할 삽화를 찾을 수 없습니다.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteIllustration(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "삭제할 삽화 ID", example = "1") @PathVariable(name = "id") Long id,
            @Parameter(description = "자녀 프로필 ID", example = "1") @RequestParam(name = "childId") Long childId
    ) {
        Long userId = userDetails.getUser().getId();
        ChildProfile child = ownershipGuard.getOwnedChildOrThrow(childId, userId);

        illustrationService.deleteIllustration(id, child);
        return ResponseEntity.ok(
                new ApiResponseDto<>(200, "삽화가 성공적으로 삭제 되었습니다.", null)
        );
    }
}

