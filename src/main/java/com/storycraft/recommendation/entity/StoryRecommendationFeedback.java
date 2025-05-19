package com.storycraft.recommendation.entity;

import com.storycraft.recommendation.dto.RecommendResponseDto;
import com.storycraft.story.entity.Story;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "recommendations")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryRecommendationFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long feedbackId;

    @Column(name = "child_id",nullable = false)
    private String childId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id",nullable = false)
    private Story story;

    @Column(name = "liked")
    private Boolean liked;

    @Column(name = "read")
    private Boolean read;

    @Column(name = "feedback_at", nullable = false)
    private LocalDateTime feedbackAt;

    @PrePersist
    protected void onCreate() {
        this.feedbackAt = LocalDateTime.now();
    }


    public RecommendResponseDto toDto() {
        return RecommendResponseDto.builder()
                .storyId(this.story.getStoryId())
                .title(this.story.getTitle())
                .summary(this.story.getContent())
                .thumbnailUrl(this.story.getThumbnailUrl())
                .build();
    }
}
