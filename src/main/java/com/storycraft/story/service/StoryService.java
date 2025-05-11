package com.storycraft.story.service;

import com.storycraft.story.dto.StoryRequestDto;
import com.storycraft.story.dto.StoryResponseDto;
import com.storycraft.story.dto.StoryUpdateDto;
import com.storycraft.story.entity.Story;
import com.storycraft.story.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoryService {

    private final StoryRepository storyRepository;

    public StoryResponseDto createStory(StoryRequestDto dto) {
        // GPT 호출 대신 더미 생성
        Story story = Story.builder()
                .childId(dto.getChildId())
                .title("AI가 생성한 제목")
                .content("AI가 생성한 내용")
                .createdAt(LocalDateTime.now())
                .build();

        Story saved = storyRepository.save(story);

        return toDto(saved);
    }

    public StoryResponseDto getStory(Long id) {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("동화를 찾을 수 없습니다."));
        return toDto(story);
    }

    public List<StoryResponseDto> getStoryList(String childId) {
        return storyRepository.findAllByChildId(childId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public StoryResponseDto updateStory(Long id, StoryUpdateDto dto) {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("동화를 찾을 수 없습니다."));

        story.setTitle(dto.getTitle());
        story.setContent(dto.getContent());
        story.setUpdatedAt(LocalDateTime.now());

        return toDto(storyRepository.save(story));
    }

    public void deleteStory(Long id) {
        storyRepository.deleteById(id);
    }

    private StoryResponseDto toDto(Story story) {
        return StoryResponseDto.builder()
                .storyId(story.getStoryId())
                .title(story.getTitle())
                .content(story.getContent())
                .createdAt(story.getCreatedAt().toString())
                .updatedAt(story.getUpdatedAt() != null ? story.getUpdatedAt().toString() : null)
                .build();
    }
}

