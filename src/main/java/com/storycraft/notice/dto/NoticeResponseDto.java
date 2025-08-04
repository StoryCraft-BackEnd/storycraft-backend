package com.storycraft.notice.dto;

import com.storycraft.notice.entity.Notice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeResponseDto {

    private Long id;
    private String title;
    private String content;
    private String importance;
    private LocalDateTime createdAt;

    public static NoticeResponseDto from(Notice notice) {
        return NoticeResponseDto.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .importance(notice.getImportance().name())
                .createdAt(notice.getCreatedAt())
                .build();
    }
} 