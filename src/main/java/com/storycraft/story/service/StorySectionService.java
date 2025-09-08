package com.storycraft.story.service;

import com.storycraft.story.dto.StorySectionDto;
import com.storycraft.story.entity.Story;
import com.storycraft.story.entity.StorySection;
import com.storycraft.story.repository.StoryRepository;
import com.storycraft.story.repository.StorySectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StorySectionService {

    private final StorySectionRepository storySectionRepository;
    private final StoryRepository storyRepository;

    //본문 컨텐츠에서 단락 구별후 단락 저장 메소드
    public void saveSectionsFromContent(Story story, String content, String contentKr) {
        List<String> enParagraphs = splitParagraph(content);    //영어 본문 단락 리스트 저장
        List<String> krParagraphs = splitParagraph(contentKr);  //한국어 본문 단락 리스트 저장

        int count = Math.min(enParagraphs.size(), krParagraphs.size()); //둘중 더 작은 갯수의 단락을 for문 범위로 설정

        List<StorySection> sections = new ArrayList<>();
        for (int i = 0; i < count; i++) {                       //단락 리스트들 순회 하면서 StorySection 리스트에 다시 넣기
            StorySection section = StorySection.builder()
                    .story(story)
                    .orderIndex(i + 1)
                    .paragraphText(enParagraphs.get(i))
                    .paragraphTextKr(krParagraphs.get(i))
                    .build();
            sections.add(section);
        }
        storySectionRepository.saveAll(sections);
    }

    public List<StorySectionDto> getSectionsByStoryId(Long storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 동화가 존재하지 않습니다."));

        return storySectionRepository.findAllByStoryOrderByOrderIndex(story).stream()
                .map(StorySectionDto::fromEntity)
                .toList();
    }

    private List<String> splitParagraph(String content) {
        return Arrays.stream(content.split("\\n\\n"))
                .map(String::trim)
                .filter(p -> !p.isBlank())
                .toList();
    }
}
