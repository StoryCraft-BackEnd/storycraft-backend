package com.storycraft.auth.controller;

import com.storycraft.auth.dto.SignupRequest;
import com.storycraft.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Validated @RequestBody SignupRequest request) {
        boolean result = authService.signup(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(201, "회원가입이 완료되었습니다.", result)
        );
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
