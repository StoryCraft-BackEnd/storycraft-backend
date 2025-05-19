package com.storycraft.story.entity;

import com.storycraft.global.entity.BaseTimeEntity;
import com.storycraft.story.dto.StoryResponseDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stories")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Story extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "story_id")
    private Long storyId;

    @Column(name = "child_id", nullable = false)
    private String childId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "story", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Illustration> illustrations;

    // 썸네일 추출 메서드
    public String getThumbnailUrl() {
        return (illustrations != null && !illustrations.isEmpty())
                ? illustrations.get(0).getImageUrl()
                : null;
    }

    // 사용자에게 반환할 API 응답을 위한 메소드 toDto
    public StoryResponseDto toDto() {
        return StoryResponseDto.builder()
                .storyId(this.getStoryId())
                .title(this.getTitle())
                .content(this.getContent())
                .thumbnailUrl(this.getThumbnailUrl())
                .createdAt(this.getCreatedAt().toString())
                .updatedAt(this.getUpdatedAt() != null ? this.getUpdatedAt().toString() : null)
                .build();
    }
}
