package com.storycraft.auth.service;

import com.storycraft.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final UserRepository userRepository;

    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
}
