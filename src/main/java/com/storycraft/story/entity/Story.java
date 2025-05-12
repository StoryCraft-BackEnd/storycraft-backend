package com.storycraft.story.entity;

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
    private Long StoryId;

    @Column(name = "child_id", nullable = false)
    private String childId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    public StoryResponseDto toDto() {
        return StoryResponseDto.builder()
                .storyId(this.getStoryId())
                .title(this.getTitle())
                .content(this.getContent())
                .createdAt(this.getCreatedAt().toString())
                .updatedAt(this.getUpdatedAt() != null ? this.getUpdatedAt().toString() : null)
                .build();
    }
}
