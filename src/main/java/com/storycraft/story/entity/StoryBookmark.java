package com.storycraft.story.entity;

import com.storycraft.profile.entity.ChildProfile;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "story_bookmarks")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoryBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id")
    private int bookmarkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private ChildProfile child;

    @Column(name = "bookmarked_at")
    private LocalDateTime bookmarkedAt;
}
