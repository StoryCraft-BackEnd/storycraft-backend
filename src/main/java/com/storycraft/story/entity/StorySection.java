package com.storycraft.story.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Story_sections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StorySection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "section_id")
    private int sectionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @Column(name = "paragraph_text", nullable = false, columnDefinition = "TEXT")
    private String paragraphText;

    @Column(name = "paragraph_text_kr", nullable = false, columnDefinition = "TEXT")
    private String paragraphTextKr;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

}
