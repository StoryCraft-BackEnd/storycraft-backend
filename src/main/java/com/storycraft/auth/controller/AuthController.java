package com.storycraft.auth.controller;

import com.storycraft.auth.dto.LoginRequestDto;
import com.storycraft.auth.dto.SignupRequest;
import com.storycraft.auth.jwt.SecurityUtil;
import com.storycraft.auth.service.AuthService;
import com.storycraft.global.response.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.storycraft.auth.dto.LoginResponseDto;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입 API", description = "새로운 부모 사용자 회원가입")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Validated @RequestBody SignupRequest request) {
        boolean result = authService.signup(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(201, "회원가입이 완료되었습니다.", result)
        );
    }

    @Operation(summary = "로그인 API", description = "회원 로그인, 인증에 성공하면 JWT 액세스 토큰 반환")
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<LoginResponseDto>> login(@RequestBody LoginRequestDto request) {
        LoginResponseDto response = authService.login(request);
        return ResponseEntity.ok(new ApiResponseDto<>(200, "로그인 성공", response));
    }

    @Operation(summary = "로그아웃 API", description = "현재 로그인한 사용자가 로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDto<Boolean>> logout(@RequestHeader("Authorization") String authorizationHeader) {
        String email = SecurityUtil.getCurrentUserEmail();
        authService.logout(email);
        return ResponseEntity.ok(new ApiResponseDto<>(200, "로그아웃이 완료되었습니다.", true));
    }



    // 응답용 DTO
    private static class ApiResponse {
        private int status;
        private String message;
        private boolean data;

        public ApiResponse(int status, String message, boolean data) {
            this.status = status;
            this.message = message;
            this.data = data;
        }

        public int getStatus() { return status; }
        public String getMessage() { return message; }
        public boolean getData() { return data; }
    }
}
