package com.storycraft.story.entity;

import com.storycraft.global.entity.BaseTimeEntity;
import com.storycraft.illustration.entity.Illustration;
import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.story.dto.StoryResponseDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private ChildProfile childId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @ElementCollection              // 동화 별 키워드 테이블 자동 생성 -> 키워드 출력용
    @CollectionTable(name = "story_keywords", joinColumns = @JoinColumn(name = "story_id"))
    @Column(name = "keyword")
    private List<String> keywords;

    @OneToMany(mappedBy = "story", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Illustration> illustrations;

    // 썸네일 추출 메서드
    public String getThumbnailUrl() {
        return (illustrations != null && !illustrations.isEmpty())
                ? illustrations.get(0).getImageUrl()
                : null;
    }

    public void updateContent(String title, String content, List<String> keywords) {
        this.title = title;
        this.content = content;
        this.keywords = keywords;
    }

    // 사용자에게 반환할 API 응답을 위한 메소드 toDto
    public StoryResponseDto toDto() {
        return StoryResponseDto.builder()
                .storyId(this.getId())
                .title(this.getTitle())
                .content(this.getContent())
                .keywords(this.getKeywords())
                .thumbnailUrl(this.getThumbnailUrl())
                .createdAt(this.getCreatedAt().toString())
                .updatedAt(this.getUpdatedAt() != null ? this.getUpdatedAt().toString() : null)
                .build();
    }
}
