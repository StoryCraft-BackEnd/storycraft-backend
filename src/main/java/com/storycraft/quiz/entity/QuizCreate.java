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

    @Column(name = "correct_answer", nullable = false, length = 1)
    private String correctAnswer;


    public static QuizCreate fromDto(Story story, QuizCreateRequestDto dto) {
        Map<String, String> opts = dto.getOptions();
        String ans = dto.getAnswer() == null ? null : dto.getAnswer().trim().toUpperCase();
        return QuizCreate.builder()
                .story(story)
                .question(dto.getQuestion())
                .optionA(opts.get("A"))
                .optionB(opts.get("B"))
                .optionC(opts.get("C"))
                .optionD(opts.get("D"))
                .correctAnswer(ans)           // "A"~"D"
                .build();
    }

    /** 보기 맵 조회 */
    public Map<String, String> getOptions() {
        return Map.of(
                "A", this.optionA,
                "B", this.optionB,
                "C", this.optionC,
                "D", this.optionD
        );
    }

    /** 보기 맵 세팅(필요 시 사용) */
    public void setOptions(Map<String, String> options) {
        if (options == null) return;
        this.optionA = options.get("A");
        this.optionB = options.get("B");
        this.optionC = options.get("C");
        this.optionD = options.get("D");
    }

    // 사용자에게 반환할 API 응답을 위한 메소드 toDto
    public QuizCreateResponseDto toDto() {
        return QuizCreateResponseDto.builder()
                .quizId(this.quizId)
                .question(this.question)
                .options(getOptions())
                // 필요하면 아래 두 줄 활성화 (DTO에 필드가 있을 때만)
                // .answer(this.correctAnswer)
                // .explanation(this.explanation)
                .build();
    }

}
