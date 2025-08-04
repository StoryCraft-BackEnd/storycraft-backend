package com.storycraft.user.repository;

import com.storycraft.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    // 이메일과 로그인 타입으로 사용자 찾기
    Optional<User> findByEmailAndLoginType(String email, String loginType);
}
