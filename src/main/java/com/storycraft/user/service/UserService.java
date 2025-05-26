package com.storycraft.user.service;

import com.storycraft.user.dto.UserInfoResponseDto;
import com.storycraft.user.entity.User;
import com.storycraft.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserInfoResponseDto getMyInfo(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return new UserInfoResponseDto(
                user.getEmail(),
                user.getName(),
                user.getNickname(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
