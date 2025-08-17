package com.storycraft.quiz.service;

import com.storycraft.ai.dto.AiQuizResponseDto;
import com.storycraft.ai.service.AiGptService;
import com.storycraft.dictionary.service.DictionaryService;
import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.profile.repository.ChildProfileRepository;
import com.storycraft.quiz.dto.*;
import com.storycraft.quiz.entity.QuizCreate;
import com.storycraft.quiz.entity.QuizSubmit;
import com.storycraft.quiz.repository.QuizCreateRepository;
import com.storycraft.quiz.repository.QuizSubmitRepository;
import com.storycraft.story.entity.Story;
import com.storycraft.story.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizCreateRepository quizCreateRepository;
    private final QuizSubmitRepository quizSubmitRepository;
    private final StoryRepository storyRepository;
    private final ChildProfileRepository childProfileRepository;
    private final AiGptService aiGptService;
    private final DictionaryService dictionaryService;

    //퀴즈 생성
    public List<QuizCreateResponseDto> createQuizList(Long storyId, ChildProfile child, List<String> keywords) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new IllegalArgumentException("동화를 찾을 수 없습니다."));

        verifyOwnershipOrThrow(story, child.getId());

        if (keywords == null || keywords.isEmpty()) {
            keywords = new ArrayList<>(dictionaryService.extractWords(story.getContent())); // Set -> List
        }
        if (keywords.isEmpty()) {
            throw new IllegalArgumentException("중요 단어가 없습니다. 동화의 ** 표시 또는 단어 추출 로직을 확인해주세요.");
        }

        List<AiQuizResponseDto> aiQuizzes =
                aiGptService.generateQuizFromContentAndKeywords(story.getContent(), keywords);

        List<QuizCreateRequestDto> dtoList = aiQuizzes.stream().map(ai -> {
            // 1) 옵션을 A,B,C,D 순으로 고정
            Map<String, String> src = ai.getOptions();
            Map<String, String> ordered = new LinkedHashMap<>();
            for (String k : List.of("A", "B", "C", "D")) {
                if (src != null && src.containsKey(k)) {
                    ordered.put(k, src.get(k));
                }
            }

            // 2) 정답 정리 및 검증
            String answer = (ai.getAnswer() == null) ? "" : ai.getAnswer().trim().toUpperCase();
            if (!ordered.containsKey(answer)) {
                throw new IllegalArgumentException("AI가 반환한 정답 키가 옵션(A~D)에 없습니다: " + answer);
            }

            // 3) DTO 구성
            QuizCreateRequestDto dto = new QuizCreateRequestDto();
            dto.setQuestion(ai.getQuestion());
            dto.setOptions(ordered);   // 순서 보장됨
            dto.setAnswer(answer);
            return dto;
        }).toList();

        return saveQuizzes(storyId, dtoList);
    }

    public List<QuizCreateResponseDto> getQuizList(Long storyId, ChildProfile child) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new IllegalArgumentException("동화를 찾을 수 없습니다."));

        verifyOwnershipOrThrow(story, child.getId());

        List<QuizCreate> quizzes = quizCreateRepository.findAllByStoryOrderByQuizIdAsc(story);

        return quizzes.stream()
                .map(q -> QuizCreateResponseDto.builder()
                        .quizId(q.getQuizId())
                        .storyId(storyId)
                        .question(q.getQuestion())
                        .options(q.getOptions())
                        .build())
                .toList();
    }

    /**
     * 퀴즈 제출
     */
    public QuizSubmitResponseDto submitQuiz(Long storyId, Long childId, List<QuizSubmitRequestDto> answers) {
        // 1) 대상 스토리/자녀 조회
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new IllegalArgumentException("동화를 찾을 수 없습니다."));
        ChildProfile child = childProfileRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("자녀를 찾을 수 없습니다."));

        verifyOwnershipOrThrow(story, childId);

        // 2) 스토리의 퀴즈 목록 조회
        // List<QuizCreate> quizzes = quizCreateRepository.findAllByStory_StoryIdOrderByQuizIdAsc(storyId);
        List<QuizCreate> quizzes = quizCreateRepository.findAllByStoryOrderByQuizIdAsc(story);

        // 3) 제출 답안 맵 (quizId -> selectedAnswer)
        Map<Long, String> selectedMap = answers.stream()
                .collect(Collectors.toMap(
                        QuizSubmitRequestDto::getQuizId,
                        QuizSubmitRequestDto::getSelectedAnswer,
                        (a, b) -> a // 중복 키 오면 첫 값 사용
                ));

        int correctCount = 0;
        List<QuizResultDto> resultDtos = new ArrayList<>();
        List<QuizSubmit> rows = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (QuizCreate q : quizzes) {
            String selected = selectedMap.get(q.getQuizId());
            String selectedSafe = (selected == null) ? "" : selected; // DB 컬럼(selected_answer)이 NOT NULL이므로 미응답이면 빈 문자열로 저장

            String correctAnswer = String.valueOf(q.getCorrectAnswer()); // char/String 어느 쪽이든 안전
            boolean isCorrect = selected != null && selected.equalsIgnoreCase(correctAnswer);
            if (isCorrect) correctCount++;

            // 응답 DTO(프런트로 보여줄 문항별 결과)
            resultDtos.add(QuizResultDto.builder()
                    .quizId(q.getQuizId())
                    .question(q.getQuestion())
                    .selectedAnswer(selected)
                    .correctAnswer(correctAnswer)
                    .isCorrect(isCorrect)
                    .build()
            );

            //문항별 결과 저장 (QuizSubmit 엔티티)
            rows.add(QuizSubmit.builder()
                    .quizCreate(q)
                    .child(child)                    // 필드명이 childId(타입은 ChildProfile)인 현재 엔티티에 맞춤
                    .selectedAnswer(selectedSafe)    // NOT NULL 제약 대응
                    .isCorrect(isCorrect)
                    .score(isCorrect ? 10 : 0)       // 정책에 맞게 점수 조정 가능
                    .submittedAt(now)
                    .build()
            );
        }

        // 4) DB 저장 (문항 개수만큼 insert)
        quizSubmitRepository.saveAll(rows);

        // 5) 제출 결과 응답
        return QuizSubmitResponseDto.builder()
                .storyId(storyId)
                .childId(childId)
                .total(quizzes.size())
                .correct(correctCount)
                .submittedAt(now.toString())
                .results(resultDtos)
                .build();
    }

    //퀴즈 결과 조회 (총 10문제 고정, 문제당 배점 10점)
    public QuizResultSummaryResponseDto getQuizResultSummary(Long storyId, Long childId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new IllegalArgumentException("동화를 찾을 수 없습니다."));

        verifyOwnershipOrThrow(story, childId);

        // 1. 동화 ID로 퀴즈 목록 조회 (총 10문제 기준)
        List<QuizCreate> quizzes = quizCreateRepository.findAllByStory_Id(storyId);

        // 2. 자녀가 제출한 퀴즈 결과 전체 조회
        List<QuizSubmit> submits = quizSubmitRepository.findByChild_Id(childId);

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



    private List<QuizCreateResponseDto> saveQuizzes(Long storyId, List<QuizCreateRequestDto> dtoList) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new IllegalArgumentException("Story not found"));

        if (dtoList == null || dtoList.isEmpty()) {
            throw new IllegalArgumentException("퀴즈 리스트가 비어있습니다.");
        }

        List<QuizCreate> entities = new ArrayList<>();
        for (QuizCreateRequestDto dto : dtoList) {
            validateCreateDto(dto);
            QuizCreate q = QuizCreate.fromDto(story, dto);
            entities.add(q);
        }

        List<QuizCreate> saved = quizCreateRepository.saveAll(entities);

        return saved.stream().map(e -> QuizCreateResponseDto.builder()
                .quizId(e.getQuizId())
                .storyId(storyId)
                .question(e.getQuestion())
                .options(e.getOptions())
                .build()
        ).toList();
    }

    private void validateCreateDto(QuizCreateRequestDto dto) {
        if (dto.getQuestion() == null || dto.getQuestion().isBlank()) {
            throw new IllegalArgumentException("문항이 비었습니다.");
        }
        if (dto.getOptions() == null || dto.getOptions().size() != 4) {
            throw new IllegalArgumentException("선택지는 A~D 4개여야 합니다.");
        }
        if (dto.getAnswer() == null || !dto.getOptions().containsKey(dto.getAnswer())) {
            throw new IllegalArgumentException("정답은 A~D 중 하나이고, 실제 옵션 키여야 합니다.");
        }
    }
}
