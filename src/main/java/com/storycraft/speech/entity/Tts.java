package com.storycraft.speech.entity;

import com.storycraft.global.entity.BaseTimeEntity;
import com.storycraft.speech.dto.TtsCreateResponseDto;
import com.storycraft.story.entity.Story;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tts_files")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tts extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tts_id")
    private Long ttsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @Column(name = "tts_url", columnDefinition = "TEXT", nullable = false)
    private String ttsUrl;

    // 사용자에게 반환할 API 응답을 위한 메소드
    public TtsCreateResponseDto toDto() {
        return TtsCreateResponseDto.builder()
                .ttsId(this.getTtsId())
                .storyId(story.getStoryId())
                .ttsUrl(this.ttsUrl)
                .createdAt(this.getCreatedAt())
                .build();
    }
}
