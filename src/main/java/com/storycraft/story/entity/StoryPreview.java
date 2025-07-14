package com.storycraft.story.entity;

import com.storycraft.global.entity.BaseTimeEntity;
import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "story_previews")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoryPreview extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "preview_id")
    private int previewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private ChildProfile child;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @Column(name = "preview_text", nullable = false, columnDefinition = "TEXT")
    private String previewText;
}
