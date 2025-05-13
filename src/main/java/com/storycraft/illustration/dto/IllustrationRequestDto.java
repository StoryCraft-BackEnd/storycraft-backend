package com.storycraft.illustration.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter @Setter
@Schema(description = "삽화 생성 요청 DTO")
public class IllustrationRequestDto {

    @Schema(description = "대상 동화 ID", example = "1")
    private Long storyId;

    @Schema(description = "동화 내용에 따른 이미지 생성 키워드", example = "숲속에서 모험중인 꼬마용사와 강아지 친구")
    private String prompt;
}
