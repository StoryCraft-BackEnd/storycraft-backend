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
    public ResponseEntity<ApiResponseDto<Boolean>> checkEmailExists(@RequestBody EmailCheckRequestDto request) {
        boolean available = emailService.isEmailAvailable(request.getEmail());
        String message = available ? "사용 가능한 이메일입니다." : "이미 사용 중인 이메일입니다.";

        ApiResponseDto<Boolean> response = new ApiResponseDto<>(
                HttpStatus.OK.value(), message, available
        );
        return ResponseEntity.ok(response);
    }


}
