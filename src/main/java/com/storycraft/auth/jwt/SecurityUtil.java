package com.storycraft.auth.jwt;

import com.storycraft.auth.service.UserDetailsImpl;
import com.storycraft.user.entity.User;
import com.storycraft.user.entity.UserRole;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Spring Security Context에서 현재 인증된 사용자 정보를 조회하는 유틸리티 클래스
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtil {

    /**
     * 현재 인증된 Authentication 객체를 반환
     * @return 인증된 Authentication 객체
     * @throws AuthenticationCredentialsNotFoundException 인증 정보가 없는 경우
     */
    private static Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null ||
            !authentication.isAuthenticated() ||
            authentication instanceof AnonymousAuthenticationToken) {
            throw new AuthenticationCredentialsNotFoundException("Security Context에 인증 정보가 없습니다.");
        }
        return authentication;
    }

    /**
     * 현재 인증된 사용자의 이메일을 반환
     * @return 사용자 이메일
     * @throws AuthenticationCredentialsNotFoundException 인증 정보를 찾을 수 없는 경우
     */
    public static String getCurrentUserEmail() {
        Object principal = getAuthentication().getPrincipal();

        if (principal instanceof UserDetailsImpl) {
            return ((UserDetailsImpl) principal).getUser().getEmail();
        } else if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            return (String) principal;
        }
        throw new AuthenticationCredentialsNotFoundException("인증된 사용자 정보를 찾을 수 없습니다.");
    }

    /**
     * 현재 인증된 사용자의 User 엔티티를 반환
     * @return User 엔티티
     * @throws AuthenticationCredentialsNotFoundException UserDetailsImpl 타입이 아닌 경우
     */
    public static User getCurrentUser() {
        Object principal = getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsImpl) {
            return ((UserDetailsImpl) principal).getUser();
        }
        throw new AuthenticationCredentialsNotFoundException("UserDetailsImpl 타입이 아닙니다.");
    }

    /**
     * 현재 인증된 사용자의 ID를 반환
     * @return 사용자 ID
     * @throws AuthenticationCredentialsNotFoundException 인증 정보를 찾을 수 없는 경우
     */
    public static Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * 현재 인증된 사용자의 UserDetailsImpl을 반환
     * @return UserDetailsImpl 객체
     * @throws AuthenticationCredentialsNotFoundException UserDetailsImpl 타입이 아닌 경우
     */
    public static UserDetailsImpl getCurrentUserDetails() {
        Object principal = getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsImpl) {
            return (UserDetailsImpl) principal;
        }
        throw new AuthenticationCredentialsNotFoundException("UserDetailsImpl 타입이 아닙니다.");
    }

    /**
     * 현재 사용자가 특정 역할을 가지고 있는지 확인
     * @param role 확인할 역할
     * @return 역할을 가지고 있으면 true, 아니면 false
     */
    public static boolean hasRole(UserRole role) {
        try {
            User user = getCurrentUser();
            return user.getRole() == role;
        } catch (AuthenticationCredentialsNotFoundException e) {
            return false;
        }
    }

    /**
     * 현재 사용자가 관리자인지 확인
     * @return 관리자이면 true, 아니면 false
     */
    public static boolean isAdmin() {
        return hasRole(UserRole.ADMIN);
    }

    /**
     * 현재 사용자가 부모인지 확인
     * @return 부모이면 true, 아니면 false
     */
    public static boolean isParent() {
        return hasRole(UserRole.PARENT);
    }

    /**
     * 현재 사용자가 인증되어 있는지 확인
     * @return 인증되어 있으면 true, 아니면 false
     */
    public static boolean isAuthenticated() {
        try {
            getAuthentication();
            return true;
        } catch (AuthenticationCredentialsNotFoundException e) {
            return false;
        }
    }
}
