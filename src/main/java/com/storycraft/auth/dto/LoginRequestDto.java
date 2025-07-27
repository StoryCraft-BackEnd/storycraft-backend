package com.storycraft.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequestDto {
    private String email;

    @Schema(description = "사용자 비밀번호 (8자 이상)", example = "test1234")
    private String password;
}
