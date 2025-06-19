package com.storycraft.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChildProfileCreateRequestDto {
    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @Min(value = 1, message = "나이는 1 이상이어야 합니다.")
    private Integer age;

    @NotBlank(message = "학습 수준은 필수입니다.")
    @Pattern(regexp = "초급|중급|고급", message = "학습 수준은 초급, 중급, 고급 중 하나여야 합니다.")
    private String learningLevel;  // "초급", "중급", "고급"
}