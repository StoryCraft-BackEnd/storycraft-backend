package com.storycraft.auth.controller;

import com.storycraft.auth.dto.*;
import com.storycraft.auth.jwt.SecurityUtil;
import com.storycraft.auth.service.AuthService;
import com.storycraft.auth.service.EmailService;
import com.storycraft.auth.service.ResetTokenService;
import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.redis.service.RedisService;
import com.storycraft.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Random;
import com.storycraft.user.repository.UserRepository;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;
    private final RedisService redisService;
    private final ResetTokenService resetTokenService;
    private final UserService userService;
    private final UserRepository userRepository;

    @Operation(summary = "회원가입 API", description = "새로운 부모 사용자 회원가입")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Validated @RequestBody SignupRequest request) {
        boolean result = authService.signup(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponseDto<>(201, "회원가입이 완료되었습니다.", result)
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

    // 1. 이메일 인증코드 발송
    @Operation(summary = "이메일 인증코드 발송", description = "사용자가 이메일을 입력하면 해당 이메일로 6자리 인증코드를 전송")
    @PostMapping("/request-reset-code")
    public ResponseEntity<?> requestResetCode(@Valid @RequestBody RequestResetCodeDto dto) {
        String code = generate6DigitCode();
        emailService.sendResetCode(dto.getEmail(), code);
        redisService.saveCode(dto.getEmail(), code);
        return ResponseEntity.ok(new ApiResponseDto<>(200, "인증 코드 발송 완료", true));
    }

    private String generate6DigitCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }


    // 2. 이메일 인증코드 검증 + 리셋 토큰 발급
    @Operation(summary = "이메일 인증코드 검증 + 리셋 토큰 발급", description = "이메일과 인증코드를 검증하고, 성공시 리셋토큰 발급")
    @PostMapping("/verify-reset-code")
    public ResponseEntity<?> verifyResetCode(@Valid @RequestBody VerifyResetCodeDto dto) {
        String storedCode = redisService.getCode(dto.getEmail());
        if (storedCode == null || !storedCode.equals(dto.getCode())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto<>(401, "인증 코드가 맞지 않거나 만료되었습니다.", null));
        }
        redisService.deleteCode(dto.getEmail());

        String resetToken = resetTokenService.createResetToken(dto.getEmail());
        return ResponseEntity.ok(new ApiResponseDto<>(200, "인증 코드 검증 성공", Map.of("resetToken", resetToken)));
    }

    // 3. 비밀번호 재설정
    @Operation(summary = "비밀번호 재설정", description = "리셋토큰을 사용해서 새로운 비밀번호 설정")
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDto dto) {
        String email = resetTokenService.verifyResetToken(dto.getResetToken());
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto<>(401, "리셋 토큰이 유효하지 않습니다.", null));
        }

        boolean updated = userService.updatePassword(email, dto.getNewPassword());
        if (!updated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>(404, "사용자 정보를 찾을 수 없습니다.", null));
        }

        return ResponseEntity.ok(new ApiResponseDto<>(200, "비밀번호가 성공적으로 변경되었습니다.", true));
    }




    // 응답용 DTO - 제거 (ApiResponseDto 사용)
}
