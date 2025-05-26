package com.storycraft.auth.service;

import com.storycraft.auth.dto.*;
import com.storycraft.auth.entity.AuthToken;
import com.storycraft.auth.jwt.JwtTokenProvider;
import com.storycraft.auth.repository.AuthTokenRepository;
import com.storycraft.global.exception.CustomException;
import com.storycraft.global.exception.ErrorCode;
import com.storycraft.user.entity.User;
import com.storycraft.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthTokenRepository authTokenRepository;

    @Transactional
    public boolean signup(SignupRequest request) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .nickname(request.getNickname())
                .role(request.getRole() != null ? request.getRole() : "parent")
                .build();

        userRepository.save(user);

        return true;
    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_LOGIN));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_LOGIN);
        }

        // Access Token 생성
        String accessToken = jwtTokenProvider.createAccessToken(user);
        // Refresh Token 생성
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        // 리프레시 토큰 저장 또는 갱신
        authTokenRepository.findByUser(user).ifPresentOrElse(
                token -> {
                    token.setRefreshToken(refreshToken);
                    authTokenRepository.save(token);
                },
                () -> authTokenRepository.save(
                        AuthToken.builder()
                                .refreshToken(refreshToken)
                                .user(user)
                                .build()
                )
        );

        return new LoginResponseDto(accessToken, refreshToken);
    }

    @Transactional
    public RefreshTokenResponseDto reissue(RefreshTokenRequestDto request) {
        String oldRefreshToken = request.getRefreshToken();

        if (!jwtTokenProvider.validateToken(oldRefreshToken)) {
            throw new RuntimeException("유효하지 않은 리프레시 토큰입니다.");
        }

        AuthToken authToken = authTokenRepository.findByRefreshToken(oldRefreshToken)
                .orElseThrow(() -> new RuntimeException("DB에 저장된 리프레시 토큰이 아닙니다."));

        User user = authToken.getUser();

        String newAccessToken = jwtTokenProvider.createAccessToken(user);
        // 새로운 Refresh Token 생성
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        // DB에 Refresh Token 갱신 저장
        authToken.setRefreshToken(newRefreshToken);
        authTokenRepository.save(authToken);

        return new RefreshTokenResponseDto(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(String email) {
        authTokenRepository.deleteByUserEmail(email);
    }
}
