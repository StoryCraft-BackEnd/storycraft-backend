package com.storycraft.quiz.service;

import com.storycraft.quiz.dto.*;
import com.storycraft.quiz.entity.QuizCreate;
import com.storycraft.quiz.entity.QuizSubmit;
import com.storycraft.quiz.repository.QuizCreateRepository;
import com.storycraft.quiz.repository.QuizSubmitRepository;
import com.storycraft.story.entity.Story;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizCreateRepository quizCreateRepository;
    private final QuizSubmitRepository quizSubmitRepository;

    /**
     *  퀴즈 생성 (GPT 기반 -> 추후 연동 예정)
     */
    public List<QuizCreateResponseDto> createQuiz(QuizCreateRequestDto dto) {

        // 임시 더미 생성 -> GPT 연동후 수정
        List<QuizCreate> quizList = List.of(
            QuizCreate.builder()
                .story(Story.builder().storyId(dto.getStoryId()).build())
                .question("Which animal went on the Adventure?")
                .optionA("The Bird")
                .optionB("The Dragon")
                .optionC("The Dog")
                .optionD("The Rabbit")
                .correctAnswer('C')     //GPT 연동후 정답-보기 매칭 자동화 필요
                .build(),


            QuizCreate.builder()
                .story(Story.builder().storyId(dto.getStoryId()).build())
                .question("What did the child carry?")
                .optionA("A book")
                .optionB("A lantern")
                .optionC("A bag")
                .optionD("A toy")
                .correctAnswer('C')
                .build()
    );

        List<QuizCreate> savedList = quizCreateRepository.saveAll(quizList);

        return savedList.stream()
                .map(QuizCreate::toDto)
                .toList();
    }

    /**
     * 퀴즈 제출
     */
    public QuizSubmitResponseDto submitQuiz(Long quizId, QuizSubmitRequestDto dto) {
        QuizCreate quiz = quizCreateRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("퀴즈를 찾을 수 없습니다."));
        boolean isCorrect = String.valueOf(quiz.getCorrectAnswer())
                .equalsIgnoreCase(dto.getSelectedAnswer());

        QuizSubmit submit = QuizSubmit.builder()
                .quizCreate(quiz)
                .childId(dto.getChildId())
                .selectedAnswer(dto.getSelectedAnswer())
                .isCorrect(isCorrect)
                .submittedAt(LocalDateTime.now())
                .build();

        return quizSubmitRepository.save(submit).toDto();
    }

    /**
     * 퀴즈 결과 조회 (총 10문제 고정, 문제당 배점 10점)
     */
    public QuizResultSummaryResponseDto getQuizResultSummary(Long storyId, ChildProfile childId) {
        // 1. 동화 ID로 퀴즈 목록 조회 (총 10문제 기준)
        List<QuizCreate> quizzes = quizCreateRepository.findAllByStory_StoryId(storyId);

        // 2. 자녀가 제출한 퀴즈 결과 전체 조회
        List<QuizSubmit> submits = quizSubmitRepository.findByChildId(childId);

        // 3. 결과 생성
        List<QuizResultDto> results = new ArrayList<>();
        int correctCount = 0;

        for (QuizCreate quiz : quizzes) {
            // 퀴즈 ID로 자녀가 제출한 결과 찾기
            QuizSubmit submit = submits.stream()
                    .filter(s -> s.getQuizCreate().getQuizId().equals(quiz.getQuizId()))
                    .findFirst()
                    .orElse(null);

            if (submit != null) {
                boolean isCorrect = submit.isCorrect();
                if (isCorrect) correctCount++;

                results.add(QuizResultDto.builder()
                        .quizId(quiz.getQuizId())
                        .question(quiz.getQuestion())
                        .selectedAnswer(submit.getSelectedAnswer())
                        .correctAnswer(String.valueOf(quiz.getCorrectAnswer()))
                        .isCorrect(isCorrect)
                        .build());
            }
        }

        // 4. 총 점수 계산 (한 문제당 10점, 만점 100점)
        int totalScore = correctCount * 10;

        return QuizResultSummaryResponseDto.builder()
                .childId(childId)
                .storyId(storyId)
                .score(totalScore)
                .totalQuiz(quizzes.size())   // 일반적으로 10
                .correctAnswers(correctCount)
                .results(results)
                .build();
    }
}
