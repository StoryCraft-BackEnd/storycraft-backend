package com.storycraft.quiz.dto;

import com.storycraft.profile.entity.ChildProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@Builder
@Schema(description = "퀴즈 결과 요약 응답 DTO", example = """
        {
          "childId": "123",
          "storyId": 1,
          "score": 80,
          "totalQuiz": 10,
          "correctAnswers": 8,
          "results": [
            {
              "quizId": 101,
              "question": "Which animal went on the Adventure?",
              "selectedAnswer": "C",
              "correctAnswer": "C",
              "isCorrect": true
            },
            {
              "quizId": 102,
              "question": "What did the child carry?",
              "selectedAnswer": "B",
              "correctAnswer": "C",
              "isCorrect": false
            }
          ]
        }
        """)
public class QuizResultSummaryResponseDto {

    @Schema(description = "퀴즈를 푼 자녀 ID", example = "123")
    private ChildProfile childId;

    @Schema(description = "퀴즈의 기반이 된 동화 ID", example = "1")
    private Long storyId;

    @Schema(description = "총 점수", example = "80")
    private int score;

    @Schema(description = "퀴즈 총 갯수", example = "10")
    private int totalQuiz;

    @Schema(description = "맞은 갯수", example = "8")
    private int correctAnswers;

    @Schema(description = "각 퀴즈별 결과 리스트")
    private List<QuizResultDto> results;
}
