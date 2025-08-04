package com.storycraft.dictionary.entity;

import com.storycraft.dictionary.dto.WordResponseDto;
import com.storycraft.dictionary.external.WordsApiClient;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "dictionary_words")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
public class DictionaryWords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "word_id")
    private Long wordId;

    @Column(name = "word", nullable = false, unique = true)
    private String word;

    @Column(name = "meaning", nullable = false, columnDefinition = "TEXT")
    private String meaning;

    @Column(name = "example", columnDefinition = "TEXT")
    private String example;

    @Column(name = "saved_at",nullable = false)
    private LocalDateTime savedAt;

    protected void onCreate() {
        this.savedAt = LocalDateTime.now();
    }

    // 사용자에게 반환할 API 응답을 위한 메소드
    public WordResponseDto toDto() {
        return WordResponseDto.builder()
                .wordId(this.getWordId())
                .word(this.getWord())
                .meaning(this.getMeaning())
                .exampleEng(this.getExampleEng())
                .exampleKor(this.getExampleKor())
                .savedAt(this.getSavedAt())
                .build();
    }
}
