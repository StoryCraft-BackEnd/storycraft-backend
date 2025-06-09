package com.storycraft.global.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * 모든 API 응답 형식을 통일하기 위한 제네릭 응답 클래스
 * @param <T>
 */
@Schema(description = "API 응답 wrapper")
@Getter
@AllArgsConstructor
public class ApiResponseDto<T> {
    @Schema(description = "HTTP 응답 상태 코드", example = "200")
    private int status;

    @Schema(description = "응답 메시지")
    private String message;

    @Schema(description = "실제 응답 데이터 (성공 시 결과)")
    private T data;
}
