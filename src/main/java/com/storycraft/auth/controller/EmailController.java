package com.storycraft.auth.controller;

import com.storycraft.auth.dto.EmailCheckRequestDto;
import com.storycraft.auth.dto.EmailCheckResponseDto;
import com.storycraft.auth.service.EmailService;
import com.storycraft.global.response.ApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/verification/exists")
    public ResponseEntity<ApiResponseDto<EmailCheckResponseDto>> checkEmailExists(@RequestBody EmailCheckRequestDto request) {
        boolean available = emailService.isEmailAvailable(request.getEmail());
        ApiResponseDto<EmailCheckResponseDto> response = new ApiResponseDto<>(
                HttpStatus.OK.value(),
                available ? "이메일 사용 가능" : "이미 사용 중인 이메일입니다.",
                new EmailCheckResponseDto(available)
        );
        return ResponseEntity.ok(response);
    }
}
