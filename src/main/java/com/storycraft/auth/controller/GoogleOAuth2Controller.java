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
@Tag(name = "Google OAuth2", description = "안드로이드용 구글 OAuth2 인증 API")
public class GoogleOAuth2Controller {

    private final GoogleOAuth2Service googleOAuth2Service;

    @Operation(summary = "구글 로그인 임시 사용자 확인", description = "구글 로그인 후 기존 사용자인지 확인하고 추가 정보 입력 필요 여부 반환")
    @PostMapping("/google/android/temp")
    public ResponseEntity<ApiResponseDto<GoogleOAuth2Service.GoogleTempUserResponse>> checkGoogleTempUser(@RequestBody GoogleIdTokenRequest request) {
        try {
            GoogleOAuth2Service.GoogleTempUserResponse response = googleOAuth2Service.createTempGoogleUser(request.getIdToken());
            return ResponseEntity.ok(new ApiResponseDto<>(200, "임시 사용자 확인 완료", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto<>(400, "ID 토큰 검증에 실패했습니다: " + e.getMessage(), null));
        }
    }

    @Operation(summary = "구글 로그인 추가 정보 입력", description = "구글 로그인 사용자의 닉네임을 입력하여 회원가입 완료 (역할은 parent로 고정)")
    @PostMapping("/google/android/complete")
    public ResponseEntity<ApiResponseDto<LoginResponseDto>> completeGoogleSignup(@RequestBody GoogleSignupRequest request) {
        try {
            LoginResponseDto response = googleOAuth2Service.completeGoogleSignup(request.getEmail(), request.getNickname(), request.getIdToken());
            return ResponseEntity.ok(new ApiResponseDto<>(200, "구글 회원가입 완료", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto<>(400, "회원가입에 실패했습니다: " + e.getMessage(), null));
        }
    }

    // 안드로이드용 Google ID 토큰 요청 DTO
    public static class GoogleIdTokenRequest {
        private String idToken;

        public String getIdToken() {
            return idToken;
        }

        public void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }

    // 구글 회원가입 완료 요청 DTO
    public static class GoogleSignupRequest {
        private String email;
        private String nickname;
        private String idToken;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getIdToken() {
            return idToken;
        }

        public void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }
}