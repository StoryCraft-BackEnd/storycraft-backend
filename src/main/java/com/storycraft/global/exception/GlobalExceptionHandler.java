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
