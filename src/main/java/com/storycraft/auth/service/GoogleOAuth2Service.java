package com.storycraft.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.storycraft.auth.dto.LoginResponseDto;
import com.storycraft.auth.jwt.JwtTokenProvider;
import com.storycraft.auth.repository.AuthTokenRepository;
import com.storycraft.auth.entity.AuthToken;
import com.storycraft.global.exception.CustomException;
import com.storycraft.global.exception.ErrorCode;
import com.storycraft.user.entity.User;
import com.storycraft.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleOAuth2Service {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthTokenRepository authTokenRepository;

    @Value("${google.oauth2.android.client-id}")
    private String androidClientId;

    /**
     * 안드로이드 앱에서 전송하는 Google ID 토큰을 처리하는 메서드
     * Google Sign-In SDK를 통해 얻은 ID 토큰을 검증하고 사용자 정보를 처리
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

    /**
     * 구글 로그인 후 추가 정보 입력을 위한 임시 사용자 생성
     */
    @Transactional
    public GoogleTempUserResponse createTempGoogleUser(String idToken) {
        Map<String, Object> userInfo = parseIdToken(idToken);
        
        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");
        String picture = (String) userInfo.get("picture");

        // 기존 사용자가 있으면 바로 로그인
        User existingUser = userRepository.findByEmail(email).orElse(null);
        if (existingUser != null) {
            return new GoogleTempUserResponse(true, "기존 사용자", existingUser.getNickname(), null);
        }

        // 임시 사용자 정보 반환 (닉네임 입력 필요)
        return new GoogleTempUserResponse(false, name, null, email);
    }

    /**
     * 구글 로그인 사용자의 추가 정보 입력 처리
     */
    @Transactional
    public LoginResponseDto completeGoogleSignup(String email, String nickname) {
        // 이메일로 임시 사용자 정보 확인
        Map<String, Object> userInfo = parseIdToken("temp"); // 실제로는 이메일로 사용자 찾기
        
        String name = (String) userInfo.get("name");
        String picture = (String) userInfo.get("picture");

        // 닉네임 중복 확인
        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }

        // 새 사용자 생성 (role은 "parent"로 고정)
        User user = createNewGoogleUser(email, name, picture, nickname, "parent");

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user);
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        // 리프레시 토큰 저장
        authTokenRepository.save(
                AuthToken.builder()
                        .refreshToken(refreshToken)
                        .user(user)
                        .build()
        );

        return new LoginResponseDto(accessToken, refreshToken);
    }

    private User createNewGoogleUser(String email, String name, String picture) {
        // 기본 닉네임 생성 (임시)
        String nickname = generateUniqueNickname(name);
        
        return createNewGoogleUser(email, name, picture, nickname, "parent");
    }

    private User createNewGoogleUser(String email, String name, String picture, String nickname, String role) {
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(UUID.randomUUID().toString())) // 랜덤 비밀번호 (구글 로그인용)
                .name(name)
                .nickname(nickname)
                .role(role != null ? role : "parent")
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
     * 안드로이드 앱에서 Google Sign-In SDK를 통해 전송된 토큰을 처리
     */
    private Map<String, Object> parseIdToken(String idToken) {
        try {
            // Google ID 토큰 검증기 생성
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), 
                    new JacksonFactory())
                    .setAudience(Collections.singletonList(androidClientId))
                    .build();

            // ID 토큰 검증
            GoogleIdToken googleIdToken = verifier.verify(idToken);
            
            if (googleIdToken != null) {
                Payload payload = googleIdToken.getPayload();
                
                // 사용자 정보 추출
                String email = payload.getEmail();
                String name = (String) payload.get("name");
                String picture = (String) payload.get("picture");
                
                log.info("Google ID 토큰 검증 성공: {}", email);
                
                return Map.of(
                    "email", email,
                    "name", name != null ? name : "사용자",
                    "picture", picture != null ? picture : ""
                );
            } else {
                log.error("Google ID 토큰 검증 실패: 유효하지 않은 토큰");
                throw new CustomException(ErrorCode.INVALID_TOKEN);
            }
            
        } catch (Exception e) {
            log.error("Google ID 토큰 검증 중 오류 발생: {}", e.getMessage());
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    // 구글 임시 사용자 응답 DTO
    public static class GoogleTempUserResponse {
        private final boolean isExistingUser;
        private final String name;
        private final String nickname;
        private final String email;

        public GoogleTempUserResponse(boolean isExistingUser, String name, String nickname, String email) {
            this.isExistingUser = isExistingUser;
            this.name = name;
            this.nickname = nickname;
            this.email = email;
        }

        // Getters
        public boolean isExistingUser() { return isExistingUser; }
        public String getName() { return name; }
        public String getNickname() { return nickname; }
        public String getEmail() { return email; }
    }
}