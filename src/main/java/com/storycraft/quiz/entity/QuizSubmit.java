package com.storycraft.quiz.entity;

import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.quiz.dto.QuizSubmitResponseDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_results")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizSubmit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long resultId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private QuizCreate quizCreate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "child_id", nullable = false)
    private ChildProfile childId;

    @Column(name = "selected_answer", nullable = false)
    private String selectedAnswer;

    @Column(name = "is_correct", nullable = false)
    private boolean isCorrect;

    @Column(name = "score")
    private int score;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    protected void onSubmit() {
        this.submittedAt = LocalDateTime.now();
    }


    // 사용자에게 반환할 API 응답을 위한 메소드 toDto
    public QuizSubmitResponseDto toDto() {
        return QuizSubmitResponseDto.builder()
                .quizId(this.quizCreate.getQuizId())
                .childId(this.childId)
                .isCorrect(this.isCorrect)
                .correctAnswer(String.valueOf(this.quizCreate.getCorrectAnswer()))
                .submittedAt(this.submittedAt.toString())
                .build();
    }

}

