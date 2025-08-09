package com.storycraft.user.entity;

/**
 * 사용자 역할을 정의하는 enum
 */
public enum UserRole {
    ADMIN("admin", "관리자"),
    PARENT("parent", "부모");

    private final String value;
    private final String description;

    UserRole(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 문자열 값으로부터 UserRole을 찾는 메서드
     * @param value 역할 문자열 ("admin" 또는 "parent")
     * @return 해당하는 UserRole
     * @throws IllegalArgumentException 유효하지 않은 역할인 경우
     */
    public static UserRole fromValue(String value) {
        if (value == null) {
            return PARENT; // 기본값
        }
        
        for (UserRole role : UserRole.values()) {
            if (role.value.equalsIgnoreCase(value)) {
                return role;
            }
        }
        
        throw new IllegalArgumentException("유효하지 않은 사용자 역할입니다: " + value);
    }

    /**
     * Spring Security 권한 문자열로 변환
     * @return "ROLE_ADMIN" 또는 "ROLE_PARENT"
     */
    public String toSpringSecurityRole() {
        return "ROLE_" + this.value.toUpperCase();
    }

    @Override
    public String toString() {
        return this.value;
    }
}
