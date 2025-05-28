package com.storycraft.auth.service;

import com.storycraft.auth.dto.RefreshTokenResponseDto;
import com.storycraft.auth.entity.AuthToken;
import com.storycraft.auth.jwt.JwtTokenProvider;
import com.storycraft.auth.repository.AuthTokenRepository;
import com.storycraft.user.entity.User;
import com.storycraft.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final AuthTokenRepository authTokenRepository;

    public RefreshTokenService(JwtTokenProvider jwtTokenProvider,
                               UserRepository userRepository,
                               AuthTokenRepository authTokenRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.authTokenRepository = authTokenRepository;
    }

    public RefreshTokenResponseDto reissueAccessToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        String email = jwtTokenProvider.getEmail(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다."));

        String newAccessToken = jwtTokenProvider.createAccessToken(user);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        // DB에 저장된 리프레시 토큰 업데이트
        AuthToken authToken = authTokenRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("리프레시 토큰 정보가 없습니다."));
        authToken.setRefreshToken(newRefreshToken);
        authTokenRepository.save(authToken);

        return new RefreshTokenResponseDto(newAccessToken, newRefreshToken);
    }

}
