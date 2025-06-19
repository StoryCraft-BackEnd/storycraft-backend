package com.storycraft.auth.service;

import com.storycraft.auth.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResetTokenService {

    private final JwtTokenProvider jwtTokenProvider;

    public String createResetToken(String email) {
        return jwtTokenProvider.createResetToken(email);
    }

    public String verifyResetToken(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            return null;
        }
        return jwtTokenProvider.getEmail(token);
    }

}
