package com.storycraft.profile.controller;

import com.storycraft.auth.jwt.SecurityUtil;
import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.profile.dto.ChildProfileCreateRequestDto;
import com.storycraft.profile.dto.ChildProfileIdResponseDto;
import com.storycraft.profile.dto.ChildProfileResponseDto;
import com.storycraft.profile.dto.ChildProfileUpdateRequestDto;
import com.storycraft.profile.service.ChildProfileService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/children")
@RequiredArgsConstructor
public class ChildProfileController {

    private final ChildProfileService childProfileService;

    @Operation(summary = "자녀 프로필 생성")
    @PostMapping
    public ResponseEntity<ApiResponseDto<ChildProfileIdResponseDto>> createChildProfile(
            @Valid @RequestBody ChildProfileCreateRequestDto request
    ) {
        String email = SecurityUtil.getCurrentUserEmail(); // 현재 로그인한 사용자 이메일
        ChildProfileIdResponseDto response = childProfileService.createChildProfile(email, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDto<>(201, "자녀 프로필이 생성되었습니다.", response));
    }

    @Operation(summary = "자녀 프로필 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<ChildProfileResponseDto>>> getChildProfiles() {
        String email = SecurityUtil.getCurrentUserEmail();
        List<ChildProfileResponseDto> response = childProfileService.getChildProfiles(email);

        return ResponseEntity.ok(
                new ApiResponseDto<>(200, "자녀 프로필 목록 조회에 성공했습니다.", response));
    }

    @Operation(summary = "자녀 프로필 개별 조회")
    @GetMapping("/{childId}")
    public ResponseEntity<ApiResponseDto<ChildProfileResponseDto>> getChildProfile(
            @PathVariable Long childId
    ) {
        String email = SecurityUtil.getCurrentUserEmail();
        ChildProfileResponseDto response = childProfileService.getChildProfile(email, childId);

        return ResponseEntity.ok(
                new ApiResponseDto<>(200, "자녀 프로필 조회에 성공했습니다.", response));
    }

    @Operation(summary = "자녀 프로필 수정")
    @PutMapping("/{childId}")
    public ResponseEntity<ApiResponseDto<ChildProfileIdResponseDto>> updateChildProfile(
            @PathVariable Long childId,
            @RequestBody ChildProfileUpdateRequestDto request
    ) {
        String email = SecurityUtil.getCurrentUserEmail();
        ChildProfileIdResponseDto response = childProfileService.updateChildProfile(email, childId, request);

        return ResponseEntity.ok(
                new ApiResponseDto<>(200, "자녀 프로필이 수정되었습니다.", response));
    }

    @Operation(summary = "자녀 프로필 삭제")
    @DeleteMapping("/{childId}")
    public ResponseEntity<ApiResponseDto<ChildProfileIdResponseDto>> deleteChildProfile(
            @PathVariable Long childId
    ) {
        String email = SecurityUtil.getCurrentUserEmail();
        ChildProfileIdResponseDto response = childProfileService.deleteChildProfile(email, childId);

        return ResponseEntity.ok(
                new ApiResponseDto<>(200, "자녀 프로필이 삭제되었습니다.", response));
    }
}
