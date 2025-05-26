package com.storycraft.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChildProfileResponseDto {
    private Long childId;
    private String name;
    private Integer age;
    private String learningLevel;
    private LocalDateTime createdAt;
}
