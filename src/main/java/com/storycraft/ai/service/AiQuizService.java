package com.storycraft.ai.service;

import com.storycraft.ai.dto.AiQuizRequestDto;
import com.storycraft.ai.dto.AiQuizResponseDto;
import com.storycraft.quiz.dto.QuizCreateRequestDto;
import com.storycraft.quiz.dto.QuizCreateResponseDto;
import com.storycraft.quiz.service.QuizService;
import com.storycraft.story.entity.Story;
import com.storycraft.story.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiQuizService {

    private final StoryRepository storyRepository;
    private final AiGptService aiGptService;
    private final QuizService quizService;

    public List<QuizCreateResponseDto> generateAndSaveQuiz(AiQuizRequestDto requestDto) {
        Story story = storyRepository.findById(requestDto.getStoryId())
                .orElseThrow(() -> new IllegalArgumentException("동화를 찾을 수 없습니다."));

        List<AiQuizResponseDto> aiQuizzes = aiGptService.generateQuizFromContentAndKeywords(
                story.getContent(), requestDto.getHighlightedWords()
        );

        List<QuizCreateRequestDto> convertedList = aiQuizzes.stream()
                .map(aiDto -> {
                    QuizCreateRequestDto dto = new QuizCreateRequestDto();
                    dto.setQuestion(aiDto.getQuestion());
                    dto.setOptions(aiDto.getOptions());
                    dto.setAnswer(aiDto.getAnswer());
                    return dto;
                })
                .toList();

        return quizService.createQuizList(requestDto.getStoryId(), convertedList);
    }
}
