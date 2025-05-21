package com.storycraft.auth.service;

import com.storycraft.auth.jwt.JwtTokenProvider;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {

    private final JwtTokenProvider jwtTokenProvider;

    public RefreshTokenService(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String reissueAccessToken(String refreshToken) {
        // 실제 서비스라면 DB/Redis에서 refreshToken 존재 여부 체크하는 로직 추가
        // 예) refreshTokenRepository.existsByToken(refreshToken)

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        return jwtTokenProvider.generateAccessTokenFromRefreshToken(refreshToken);
    }
}
