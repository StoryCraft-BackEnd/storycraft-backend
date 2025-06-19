package com.storycraft.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChildProfileUpdateRequestDto {
    private String name;
    private Integer age;
    private String learningLevel;
}