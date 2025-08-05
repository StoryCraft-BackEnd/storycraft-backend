package com.storycraft.story.entity;

import com.storycraft.profile.entity.ChildProfile;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "story_progress")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private ChildProfile child;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @Column(name = "learned_minutes",nullable = false)
    private int learnedMinutes;

    @Column(name = "learned_seconds", nullable = false)
    private int learnedSeconds;

    @Column(name = "read_at")
    private LocalDateTime readAt;
}
