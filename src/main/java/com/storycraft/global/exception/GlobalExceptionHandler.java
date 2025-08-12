package com.storycraft.global.exception;

import com.storycraft.global.response.ApiResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 우리가 정의한 CustomException 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponseDto<?>> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        ApiResponseDto<?> response = new ApiResponseDto<>(
                errorCode.getHttpStatus().value(),
                errorCode.getMessage(),
                null
        );
        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }

    // 입력값 검증 실패 (400 Bad Request)
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ApiResponseDto<?>> handleValidationException(Exception e) {
        ApiResponseDto<?> response = new ApiResponseDto<>(
                HttpStatus.BAD_REQUEST.value(),
                "입력값이 올바르지 않습니다.",
                null
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 타입 변환 실패 (400 Bad Request)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponseDto<?>> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        ApiResponseDto<?> response = new ApiResponseDto<>(
                HttpStatus.BAD_REQUEST.value(),
                "잘못된 파라미터 타입입니다.",
                null
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 401 Unauthorized - 인증 문제 (Spring Security)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponseDto<?>> handleAuthentication(AuthenticationException e) {
        log.warn("Authentication failed: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponseDto<>(401, "인증이 필요합니다.", null));
    }

    // 401 Unauthorized - JWT 라이브러리 계열
    @ExceptionHandler({ExpiredJwtException.class, JwtException.class})
    public ResponseEntity<ApiResponseDto<?>> handleJwt(JwtException e) {
        log.warn("JWT error: {}", e.getMessage());
        String msg = (e instanceof ExpiredJwtException) ? "토큰이 만료되었습니다." : "유효하지 않은 토큰입니다.";
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponseDto<>(401, msg, null));
    }

    // 403 Forbidden - 인가 실패 (소유 검증 실패 등)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseDto<?>> handleAccessDenied(AccessDeniedException e) {
        log.warn("Access denied: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ApiResponseDto<>(403, "접근 권한이 없습니다.", null));
    }

    // 404 Not Found - 엔티티 없음
    @ExceptionHandler({EntityNotFoundException.class, java.util.NoSuchElementException.class})
    public ResponseEntity<ApiResponseDto<?>> handleNotFound(RuntimeException e) {
        log.warn("Not found: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponseDto<>(404, "요청한 리소스를 찾을 수 없습니다.", null));
    }

    // 409 Conflict - 무결성/중복 등 제약 위반
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponseDto<?>> handleConflict(DataIntegrityViolationException e) {
        log.warn("Data integrity violation: {}", e.getMostSpecificCause().getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiResponseDto<>(409, "데이터 제약 조건을 위반했습니다.", null));
    }

    // 400 Bad Request - 바디 파싱 오류(JSON 등)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseDto<?>> handleUnreadable(HttpMessageNotReadableException e) {
        log.warn("Malformed JSON: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<>(400, "요청 본문(JSON) 형식이 올바르지 않습니다.", null));
    }

    // 400 Bad Request - @RequestParam 누락
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponseDto<?>> handleMissingParam(MissingServletRequestParameterException e) {
        log.warn("Missing request param: {}", e.getParameterName());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<>(400, "필수 파라미터가 누락되었습니다: " + e.getParameterName(), null));
    }

    // 400 Bad Request - @Validated(파라미터/경로 변수) 제약 위반
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponseDto<?>> handleConstraintViolation(ConstraintViolationException e) {
        log.warn("Constraint violations: {}", e.getMessage());
        String message = e.getConstraintViolations()
                .stream().findFirst()
                .map(v -> v.getPropertyPath() + " " + v.getMessage())
                .orElse("입력값이 올바르지 않습니다.");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<>(400, message, null));
    }

    // 그 외 예기치 못한 예외 처리 (500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<?>> handleException(Exception e) {
        e.printStackTrace(); // 디버깅을 위해 콘솔에 출력

        ApiResponseDto<?> response = new ApiResponseDto<>(
                ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus().value(),
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                null
        );
        return new ResponseEntity<>(response, ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus());
    }
}
