package com.storycraft.dictionary.entity;

import com.storycraft.dictionary.dto.SaveWordResponseDto;
import com.storycraft.profile.entity.ChildProfile;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "saved_words")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
public class SavedWords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "saved_id")
    private Long savedId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "child_id", nullable = false)
    private Long childId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", nullable = false)
    private DictionaryWords word;

    @Column(name = "saved_at",nullable = false)
    private LocalDateTime savedAt;

    protected void onCreate() {
        this.savedAt = LocalDateTime.now();
    }

    // 사용자에게 반환할 API 응답을 위한 메소드
    public SaveWordResponseDto toDto() {
        return SaveWordResponseDto.builder()
                .savedId(this.getSavedId())
                .childId(this.getChildId())
                .word(this.word.getWord())
                .meaning(this.word.getMeaning())
                .example(this.word.getExample())
                .savedAt(this.getSavedAt())
                .build();
    }

}
