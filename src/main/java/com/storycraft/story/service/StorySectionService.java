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

    public void saveSectionsFromContent(Story story, String content, String contentKr) {
        List<String> enParagraphs = splitParagraph(content);
        List<String> krParagraphs = splitParagraph(contentKr);

        int count = Math.min(enParagraphs.size(), krParagraphs.size());

        List<StorySection> sections = new ArrayList<>();
        for (int i = 0; i < paragraphs.size(); i++) {
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
