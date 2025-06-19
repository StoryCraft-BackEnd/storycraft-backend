package com.storycraft.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordDto {
    @NotBlank
    private String resetToken;

    @NotBlank
    private String newPassword;
}
