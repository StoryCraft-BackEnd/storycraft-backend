package com.storycraft.user.controller;

import com.storycraft.auth.jwt.SecurityUtil;
import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.user.dto.NicknameCheckRequestDto;
import com.storycraft.user.dto.NicknameUpdateRequestDto;
import com.storycraft.user.dto.UserInfoResponseDto;
import com.storycraft.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo(Principal principal) {
        UserInfoResponseDto userInfo = userService.getMyInfo(principal.getName());
        return ResponseEntity.ok(new ApiResponseDto<>(200, "내 정보 조회 성공", userInfo));
    }

    @PatchMapping("/users")
    public ResponseEntity<?> updateNickname(@RequestBody NicknameUpdateRequestDto request) {
        String email = SecurityUtil.getCurrentUserEmail();
        userService.updateNickname(email, request.getNickname());
        return ResponseEntity.ok(new ApiResponseDto<>(200, "닉네임이 수정되었습니다", true));
    }


}
