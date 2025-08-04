package com.storycraft.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 공통
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 HTTP 메서드입니다."),

    // 사용자 관련
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),

    // 로그인 관련
    INVALID_LOGIN(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 일치하지 않습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    GOOGLE_LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED, "구글 계정으로 로그인해주세요."),

    // JWT 관련
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "DB에 저장된 토큰이 아닙니다."),

    // 이메일 관련
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 발송에 실패했습니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 이메일입니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 등록된 이메일입니다."),

    // 비밀번호 재설정 관련
    INVALID_RESET_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리셋 토큰입니다."),
    INVALID_VERIFICATION_CODE(HttpStatus.UNAUTHORIZED, "유효하지 않은 인증 코드입니다."),
    EXPIRED_VERIFICATION_CODE(HttpStatus.UNAUTHORIZED, "인증 코드가 만료되었습니다."),
    VERIFICATION_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "인증 코드를 찾을 수 없습니다."),

    // 자녀 프로필 관련
    CHILD_PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "자녀 프로필을 찾을 수 없습니다."),
    CHILD_PROFILE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 자녀 프로필에 대한 접근 권한이 없습니다."),
    INVALID_LEARNING_LEVEL(HttpStatus.BAD_REQUEST, "유효하지 않은 학습 수준입니다."),

    // 공지사항 관련
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "공지사항을 찾을 수 없습니다."),

    // 이벤트 관련
    EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "이벤트를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
