package com.storycraft.illustration.entity;

import com.storycraft.global.entity.BaseTimeEntity;
import com.storycraft.illustration.dto.IllustrationResponseDto;
import com.storycraft.story.entity.Story;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "illustrations")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Illustration extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "illustration_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @Column(name = "order_index")
    private int orderIndex;

    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "description",columnDefinition = "TEXT")
    private String description;

    // 사용자에게 반환할 API 응답을 위한 메소드
    public IllustrationResponseDto toDto() {
        return IllustrationResponseDto.builder()
                .illustrationId(this.getId())
                .storyId(story.getId())
                .orderIndex(this.getOrderIndex())
                .imageUrl(this.getImageUrl())
                .description(this.getDescription())
                .createdAt(this.getCreatedAt().toString())
                .build();
    }
}
