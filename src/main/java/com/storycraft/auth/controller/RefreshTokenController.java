package com.storycraft.auth.controller;

import com.storycraft.auth.dto.RefreshTokenRequestDto;
import com.storycraft.auth.dto.RefreshTokenResponseDto;
import com.storycraft.auth.service.RefreshTokenService;
import com.storycraft.global.response.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;

    public RefreshTokenController(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }
    @Operation(summary = "액세스 토큰 재발급", description = "만료된 액세스 토큰을 리프레시 토큰을 이용하여 재발급")
    @PostMapping("/token/refresh")
    public ResponseEntity<ApiResponseDto<RefreshTokenResponseDto>> reissueAccessToken(@RequestBody RefreshTokenRequestDto request) {
        RefreshTokenResponseDto data = refreshTokenService.reissueAccessToken(request.getRefreshToken());

        ApiResponseDto<RefreshTokenResponseDto> response = new ApiResponseDto<>(
                HttpStatus.OK.value(),
                "액세스 토큰이 재발급되었습니다.",
                data
        );

        return ResponseEntity.ok(response);
    }

}
