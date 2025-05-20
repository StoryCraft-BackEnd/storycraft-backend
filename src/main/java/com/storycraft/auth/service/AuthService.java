package com.storycraft.auth.service;

import com.storycraft.user.entity.User;
import com.storycraft.user.repository.UserRepository;
import com.storycraft.auth.dto.SignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public boolean signup(SignupRequest request) {

        // 혹시 중복된 이메일 있으면 예외처리 (안전장치)
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
}
