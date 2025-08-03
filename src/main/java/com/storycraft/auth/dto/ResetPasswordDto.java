package com.storycraft.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "비밀번호 재설정 요청")
public class ResetPasswordDto {
    @NotBlank
    @Schema(description = "인증코드 검증 후 받은 리셋 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String resetToken;

    @NotBlank
    @Schema(description = "새로운 비밀번호 (8자 이상)", example = "newPassword123")
    private String newPassword;
}
