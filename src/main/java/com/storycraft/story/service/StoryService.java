package com.storycraft.story.service;

import com.storycraft.ai.dto.StoryContentDto;
import com.storycraft.ai.service.AiGptService;
import com.storycraft.story.dto.StoryRequestDto;
import com.storycraft.story.dto.StoryResponseDto;
import com.storycraft.story.dto.StoryUpdateDto;
import com.storycraft.story.entity.Story;
import com.storycraft.story.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoryService {

    private final StoryRepository storyRepository;
    private final AiGptService aiGptService;

    // 동화 생성
    public StoryResponseDto createStory(StoryRequestDto dto) {
        StoryContentDto result = aiGptService.generateStoryContent(Collections.singletonList(dto.getPrompt()));

        Story story = Story.builder()
                .childId(dto.getChildId())
                .title(result.getTitle())
                .content(result.getContent())
                .build();

        return storyRepository.save(story).toDto();
    }

    // 동화 상세 조회
    public StoryResponseDto getStory(Long id) {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("동화를 찾을 수 없습니다."));
        return story.toDto();
    }

    // 동화 목록 조회
    public List<StoryResponseDto> getStoryList(String childId) {
        return storyRepository.findAllByChildId(childId).stream()
                .map(Story::toDto)
                .collect(Collectors.toList());
    }

    // 동화 수정
    public StoryResponseDto updateStory(Long id, StoryUpdateDto dto) {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("동화를 찾을 수 없습니다."));

        story.setTitle(dto.getTitle());
        story.setContent(dto.getContent());

        return storyRepository.save(story).toDto();
    }

    // 동화 삭제
    public void deleteStory(Long id) {
        storyRepository.deleteById(id);
    }
}

