package com.storycraft.ai.service;

import com.storycraft.ai.dto.AiQuizRequestDto;
import com.storycraft.ai.dto.AiQuizResponseDto;
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

    public List<AiQuizResponseDto> generateQuizFromStory(AiQuizRequestDto requestDto) {
        Story story = storyRepository.findById(requestDto.getStoryId())
                .orElseThrow(() -> new IllegalArgumentException("동화를 찾을 수 없습니다."));

        String content = story.getContent();
        List<String> keywords = requestDto.getHighlightedWords();

        return aiGptService.generateQuizFromContentAndKeywords(content, keywords);
    }
}
