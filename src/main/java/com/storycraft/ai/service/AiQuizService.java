package com.storycraft.ai.service;

import com.storycraft.ai.dto.AiQuizRequestDto;
import com.storycraft.ai.dto.AiQuizResponseDto;
import com.storycraft.dictionary.service.DictionaryService;
import com.storycraft.quiz.dto.QuizCreateRequestDto;
import com.storycraft.quiz.dto.QuizCreateResponseDto;
import com.storycraft.quiz.service.QuizService;
import com.storycraft.story.entity.Story;
import com.storycraft.story.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiQuizService {

    private final StoryRepository storyRepository;
    private final ChildProfileRepository childProfileRepository;
    private final AiGptService aiGptService;
    private final QuizService quizService;
    private final DictionaryService dictionaryService;

    public List<QuizCreateResponseDto> generateAndSaveQuiz(AiQuizRequestDto requestDto) {
        Story story = storyRepository.findById(requestDto.getStoryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 동화를 찾을 수 없습니다."));

        ChildProfile child = childProfileRepository.findById(requestDto.getChildId())
                .orElseThrow(() -> new IllegalArgumentException("해당 자녀를 찾을 수 없습니다."));

        List<String> keywords = (requestDto.getHighlightedWords() != null && !requestDto.getHighlightedWords().isEmpty())
                ? requestDto.getHighlightedWords()
                : new ArrayList<>(dictionaryService.extractWordsByStoryId(requestDto.getStoryId()));

        List<AiQuizResponseDto> aiQuizzes = aiGptService.generateQuizFromContentAndKeywords(
                story.getContent(), keywords
        );

        return quizService.createQuizList(requestDto.getStoryId(), child, keywords);
    }
}
