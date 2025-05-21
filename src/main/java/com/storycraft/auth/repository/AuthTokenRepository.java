package com.storycraft.auth.repository;

import com.storycraft.auth.entity.AuthToken;
import com.storycraft.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {
    Optional<AuthToken> findByRefreshToken(String refreshToken);
    Optional<AuthToken> findByUser(User user);
}
