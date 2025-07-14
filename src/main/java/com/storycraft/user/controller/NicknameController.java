package com.storycraft.user.controller;

import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.user.dto.NicknameCheckRequestDto;
import com.storycraft.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/nickname")
public class NicknameController {

    private final UserService userService;

    public NicknameController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "닉네임 중복확인")
    @PostMapping("/exists")
    public ResponseEntity<ApiResponseDto<Boolean>> checkNickname(@RequestBody NicknameCheckRequestDto request) {
        boolean exists = userService.isNicknameExists(request.getNickname());
        String message = exists ? "이미 사용 중인 닉네임입니다." : "사용 가능한 닉네임입니다.";
        return ResponseEntity.ok(new ApiResponseDto<>(200, message, !exists));
    }
}
