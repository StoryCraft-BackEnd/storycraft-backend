package com.storycraft.quiz.entity;

import com.storycraft.global.entity.BaseTimeEntity;
import com.storycraft.quiz.dto.QuizCreateRequestDto;
import com.storycraft.quiz.dto.QuizCreateResponseDto;
import com.storycraft.story.entity.Story;
import jakarta.persistence.*;
import lombok.*;

import java.util.Map;

@Entity
@Table(name = "quizzes")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizCreate extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_id")
    private Long quizId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @Column(name = "question", nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(name = "option_a", nullable = false)
    private String optionA;

    @Column(name = "option_b", nullable = false)
    private String optionB;

    @Column(name = "option_c", nullable = false)
    private String optionC;

    @Column(name = "option_d", nullable = false)
    private String optionD;

    @Column(name = "correct_answer", nullable = false)
    private char correctAnswer;

    // 사용자에게 반환할 API 응답을 위한 메소드 toDto
    public QuizCreateResponseDto toDto() {
        return QuizCreateResponseDto.builder()
                .quizId(this.quizId)
                .question(this.question)
                .options(Map.of(
                        "A", this.optionA,
                        "B", this.optionB,
                        "C", this.optionC,
                        "D", this.optionD
                ))
                .build();
    }

}
