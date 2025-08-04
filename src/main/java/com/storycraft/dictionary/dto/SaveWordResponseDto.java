package com.storycraft.dictionary.dto;

import com.storycraft.profile.entity.ChildProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
@Schema(description = "단어 저장 응답 DTO")
public class SaveWordResponseDto {

    @Schema(description = "단어 저장 ID", example = "1")
    private Long savedId;

    @Schema(description = "자녀 ID", example = "123")
    private Long childId;

    @Schema(description = "단어", example = "adventure")
    private String word;

    @Schema(description = "뜻", example = "모험")
    private String meaning;

    @Schema(description = "예문_영어", example = "He went on an adventure through the forest.")
    private String exampleEng;

    @Schema(description = "예문_한글", example = "그는 숲을 가로질러 모험을 떠났어요.")
    private String exampleKor;

    @Schema(description = "저장 시각", example = "2025-05-18T16:49:31")
    private LocalDateTime savedAt;
}