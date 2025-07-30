package com.storycraft.auth.controller;

import com.storycraft.auth.dto.LoginResponseDto;
import com.storycraft.auth.service.GoogleOAuth2Service;
import com.storycraft.global.response.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/oauth2")
@RequiredArgsConstructor
@Tag(name = "Google OAuth2", description = "Expo와 Native Android용 구글 OAuth2 인증 API")
public class GoogleOAuth2Controller {

    private final GoogleOAuth2Service googleOAuth2Service;

    @Operation(summary = "구글 로그인", description = "Expo 또는 Native Android에서 전송하는 ID 토큰을 처리하여 JWT 토큰 반환")
    @PostMapping("/google/android")
    public ResponseEntity<ApiResponseDto<LoginResponseDto>> googleLogin(@RequestBody GoogleIdTokenRequest request) {
        try {
            LoginResponseDto response = googleOAuth2Service.processGoogleIdToken(request.getIdToken());
            return ResponseEntity.ok(new ApiResponseDto<>(200, "구글 로그인 성공", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto<>(400, "ID 토큰 검증에 실패했습니다: " + e.getMessage(), null));
        }
    }

    // Google ID 토큰 요청 DTO (Expo와 Native Android 모두 사용)
    public static class GoogleIdTokenRequest {
        private String idToken;

        public String getIdToken() {
            return idToken;
        }

        public void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }
}