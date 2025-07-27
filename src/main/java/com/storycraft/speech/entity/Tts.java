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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    private StorySection section;

    @Column(name = "voice_id")
    private String voiceId;

    @Column(name = "speech_rate")
    private float speechRate;

    @Column(name = "language", nullable = false)
    private String language;

    @Column(name = "tts_url", columnDefinition = "TEXT", nullable = false)
    private String ttsUrl;

    // 사용자에게 반환할 API 응답을 위한 메소드
    public TtsCreateResponseDto toDto() {
        return TtsCreateResponseDto.builder()
                .ttsId(this.getTtsId())
                .storyId(story.getId())
                .sectionId(section != null ? (long) section.getSectionId() : null)
                .voiceId(this.voiceId)
                .speechRate(this.speechRate)
                .language(this.language)
                .ttsUrl(this.ttsUrl)
                .createdAt(this.getCreatedAt())
                .build();
    }
}
