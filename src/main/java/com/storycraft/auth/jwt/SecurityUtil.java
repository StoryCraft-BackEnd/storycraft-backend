package com.storycraft.auth.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtil {

    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Security Context에 인증 정보가 없습니다.");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername(); // email이 username 역할
        } else if (principal instanceof String) {
            return (String) principal;
        } else {
            throw new RuntimeException("인증된 사용자 정보를 찾을 수 없습니다.");
        }
    }
}
