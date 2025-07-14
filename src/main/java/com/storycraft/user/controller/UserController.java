package com.storycraft.user.controller;

import com.storycraft.auth.jwt.SecurityUtil;
import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.user.dto.NicknameCheckRequestDto;
import com.storycraft.user.dto.NicknameUpdateRequestDto;
import com.storycraft.user.dto.UserInfoResponseDto;
import com.storycraft.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보 조회")
    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo(Principal principal) {
        UserInfoResponseDto userInfo = userService.getMyInfo(principal.getName());
        return ResponseEntity.ok(new ApiResponseDto<>(200, "내 정보 조회 성공", userInfo));
    }

    @Operation(summary = "닉네임 수정")
    @PatchMapping("")
    public ResponseEntity<ApiResponseDto<Boolean>> updateNickname(
            @RequestBody NicknameUpdateRequestDto request,
            @RequestHeader("Authorization") String authorizationHeader
    ) {

        String email = SecurityUtil.getCurrentUserEmail();

        userService.updateNickname(email, request.getNickname());
        return ResponseEntity.ok(new ApiResponseDto<>(200, "닉네임 수정이 완료되었습니다.", true));
    }

    @Operation(summary = "회원 탈퇴")
    @DeleteMapping("")
    public ResponseEntity<ApiResponseDto<Boolean>> deleteUser(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String email = SecurityUtil.getCurrentUserEmail();
        userService.deleteUser(email);
        return ResponseEntity.ok(new ApiResponseDto<>(200, "회원 탈퇴가 완료되었습니다.", true));
    }


}
