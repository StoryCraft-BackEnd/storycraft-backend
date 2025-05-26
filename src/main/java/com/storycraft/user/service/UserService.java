package com.storycraft.user.service;

import com.storycraft.global.exception.CustomException;
import com.storycraft.global.exception.ErrorCode;
import com.storycraft.user.dto.UserInfoResponseDto;
import com.storycraft.user.entity.User;
import com.storycraft.user.repository.UserRepository;
import jakarta.transaction.Transactional;
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

    public boolean isNicknameExists(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    @Transactional
    public void updateNickname(String email, String newNickname) {
        if (userRepository.existsByNickname(newNickname)) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.setNickname(newNickname);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        userRepository.delete(user);
    }



}
