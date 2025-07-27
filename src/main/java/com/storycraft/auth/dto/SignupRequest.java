package com.storycraft.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {

    @Schema(description = "사용자 이메일", example = "test@example.com", required = true)
    @Email(message = "유효한 이메일 주소를 입력하세요.")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @Schema(description = "사용자 비밀번호 (8자 이상)", example = "test1234", required = true)
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    private String password;

    @Schema(description = "사용자 이름", example = "testName", required = true)
    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @Schema(description = "사용자 닉네임", example = "testNickName", required = true)
    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;

    @Schema(description = "사용자 역할 (기본값은 'parent')", example = "parent", defaultValue = "parent")
    private String role = "parent"; // 기본값 parent로 세팅
}
