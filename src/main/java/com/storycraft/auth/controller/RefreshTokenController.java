package com.storycraft.auth.controller;

import com.storycraft.auth.dto.RefreshTokenRequestDto;
import com.storycraft.auth.dto.RefreshTokenResponseDto;
import com.storycraft.auth.service.RefreshTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;

    public RefreshTokenController(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<RefreshTokenResponseDto> reissueAccessToken(@RequestBody RefreshTokenRequestDto request) {
        String newAccessToken = refreshTokenService.reissueAccessToken(request.getRefreshToken());
        RefreshTokenResponseDto response = new RefreshTokenResponseDto(200, "액세스 토큰이 재발급되었습니다.", newAccessToken);
        return ResponseEntity.ok(response);
    }
}
