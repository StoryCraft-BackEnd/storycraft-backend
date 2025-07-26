package com.storycraft.story.service;

import com.storycraft.ai.dto.StoryContentDto;
import com.storycraft.ai.service.AiGptService;
import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.profile.repository.ChildProfileRepository;
import com.storycraft.story.dto.StoryRequestDto;
import com.storycraft.story.dto.StoryResponseDto;
import com.storycraft.story.dto.StoryUpdateDto;
import com.storycraft.story.entity.Story;
import com.storycraft.story.repository.StoryRepository;
import com.storycraft.story.repository.StorySectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoryService {

    private final StoryRepository storyRepository;
    private final AiGptService aiGptService;
    private final StorySectionService storySectionService;
    private final ChildProfileRepository childProfileRepository;
    private final StorySectionRepository storySectionRepository;

    // 동화 생성
    public StoryResponseDto createStory(StoryRequestDto dto) {
        StoryContentDto result = aiGptService.generateStoryContent(dto.getKeywords());

        ChildProfile child = childProfileRepository.findById(dto.getChildId())
                .orElseThrow(() -> new RuntimeException("해당 ID의 아이 프로필을 찾을 수 없습니다"));

        Story story = Story.builder()
                .childId(child)
                .title(result.getTitle())
                .content(result.getContent())
                .contentKr(result.getContentKr())
                .keywords(dto.getKeywords())
                .build();

        storyRepository.save(story);

        storySectionService.saveSectionsFromContent(story, result.getContent(), result.getContentKr());

        return story.toDto();
    }

    // 동화 상세 조회
    public StoryResponseDto getStory(Long id) {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("동화를 찾을 수 없습니다."));
        return story.toDto();
    }

    // 동화 목록 조회
    public List<StoryResponseDto> getStoryList(ChildProfile childId) {
        return storyRepository.findAllByChildId(childId).stream()
                .map(Story::toDto)
                .collect(Collectors.toList());
    }

    // 동화 수정
    public StoryResponseDto updateStory(Long id, StoryUpdateDto dto) {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("동화를 찾을 수 없습니다."));

        //새로운 제목/내용 생성
        StoryContentDto updatedStory = aiGptService.generateStoryContent(dto.getKeywords());

        //동화 업데이트
        story.updateContent(updatedStory.getTitle(), updatedStory.getContent(), dto.getKeywords());

        //기존 단락 삭제
        storySectionRepository.deleteAllByStory(story);

        //새로 생성된 단락 저장
        storySectionService.saveSectionsFromContent(story, updatedStory.getContent());

        return storyRepository.save(story).toDto();
    }

    // 동화 삭제
    @Transactional
    public void deleteStory(Long id) {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 동화를 찾을 수 없습니다."));

        storySectionRepository.deleteAllByStory(story);

        storyRepository.delete(story);
    }

    // 통합 조회 등을 위한 엔티티 직접 조회용 메서드
    public Story getStoryEntityById(Long id) {
        return storyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("동화를 찾을 수 없습니다."));
    }
}

