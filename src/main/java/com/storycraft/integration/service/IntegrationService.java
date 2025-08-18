package com.storycraft.integration.service;

import com.storycraft.dictionary.dto.SaveWordResponseDto;
import com.storycraft.dictionary.service.DictionaryService;
import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.profile.repository.ChildProfileRepository;
import com.storycraft.quiz.service.QuizService;
import com.storycraft.story.dto.StoryRequestDto;
import com.storycraft.story.dto.StoryResponseDto;
import com.storycraft.story.service.StoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IntegrationService {

    private final ChildProfileRepository childProfileRepository;
    private final StoryService storyService;
    private final DictionaryService dictionaryService;
    private final QuizService quizService;

    public StoryResponseDto createStoryAndWordsAndQuizzes(ChildProfile child, StoryRequestDto dto) {
        StoryResponseDto storyDto = storyService.createStory(child, dto);

        List<SaveWordResponseDto> words = dictionaryService.extractWordsAndSave(storyDto.getStoryId(), child.getId());

        List<String> keywords = words.stream()
                .map(SaveWordResponseDto::getWord)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.toList());

        quizService.createQuizList(storyDto.getStoryId(), child, keywords);

        return storyDto;
    }
}
