package com.storycraft.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserInfoResponseDto {
    private String email;
    private String name;
    private String nickname;
    private String role;
    private LocalDateTime signup_date;
}