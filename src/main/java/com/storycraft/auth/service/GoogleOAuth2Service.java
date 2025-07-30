package com.storycraft.auth.service;

import com.storycraft.auth.dto.LoginResponseDto;
import com.storycraft.auth.jwt.JwtTokenProvider;
import com.storycraft.auth.repository.AuthTokenRepository;
import com.storycraft.auth.entity.AuthToken;
import com.storycraft.global.exception.CustomException;
import com.storycraft.global.exception.ErrorCode;
import com.storycraft.user.entity.User;
import com.storycraft.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoogleOAuth2Service {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthTokenRepository authTokenRepository;

    /**
     * Google ID 토큰을 처리하는 메서드
     * Expo와 Native Android 모두에서 전송되는 ID 토큰을 처리
     */
    @Transactional
    public LoginResponseDto processGoogleIdToken(String idToken) {
        // Google ID 토큰 검증 및 사용자 정보 추출
        Map<String, Object> userInfo = parseIdToken(idToken);
        
        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");
        String picture = (String) userInfo.get("picture");

        // 기존 사용자 확인
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createNewGoogleUser(email, name, picture));

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user);
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

    private User createNewGoogleUser(String email, String name, String picture) {
        // 닉네임 중복 확인 및 생성
        String nickname = generateUniqueNickname(name);
        
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(UUID.randomUUID().toString())) // 랜덤 비밀번호
                .name(name)
                .nickname(nickname)
                .role("parent")
                .build();

        return userRepository.save(user);
    }

    private String generateUniqueNickname(String name) {
        String baseNickname = name != null ? name : "사용자";
        String nickname = baseNickname;
        int counter = 1;

        while (userRepository.existsByNickname(nickname)) {
            nickname = baseNickname + counter;
            counter++;
        }

        return nickname;
    }

    /**
     * Google ID 토큰을 파싱하고 검증하는 메서드
     * Expo와 Native Android 모두에서 전송되는 토큰을 처리
     */
    private Map<String, Object> parseIdToken(String idToken) {
        // TODO: 실제 Google ID 토큰 검증 로직 구현 필요
        // 현재는 임시 구현으로 테스트용 사용자 정보 반환
        
        // 실제 구현에서는 다음 단계가 필요:
        // 1. Google의 공개키 다운로드 및 캐싱
        // 2. ID 토큰 서명 검증
        // 3. 토큰 만료 시간 확인
        // 4. 발급자(issuer) 확인
        // 5. 클라이언트 ID 확인
        
        // 임시로 테스트용 사용자 정보 반환
        return Map.of(
            "email", "test@example.com",
            "name", "테스트 사용자",
            "picture", "https://example.com/profile.jpg"
        );
    }
}