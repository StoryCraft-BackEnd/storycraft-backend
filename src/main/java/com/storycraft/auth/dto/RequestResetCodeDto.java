package com.storycraft.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequestResetCodeDto {
    @Email
    @NotBlank
    private String email;
}