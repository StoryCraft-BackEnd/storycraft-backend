package com.storycraft.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "이메일 인증코드 검증 요청")
public class VerifyResetCodeDto {
    @Email
    @NotBlank
    @Schema(description = "인증코드를 받은 이메일 주소", example = "user@example.com")
    private String email;

    @NotBlank
    @Schema(description = "이메일로 받은 6자리 인증코드", example = "123456")
    private String code;
}