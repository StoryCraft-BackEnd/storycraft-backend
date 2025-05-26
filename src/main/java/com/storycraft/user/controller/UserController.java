package com.storycraft.user.controller;

import com.storycraft.global.response.ApiResponseDto;
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
}
