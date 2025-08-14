package com.storycraft.illustration.service;


import com.storycraft.ai.service.AiDalleService;
import com.storycraft.illustration.dto.IllustrationResponseDto;
import com.storycraft.illustration.dto.SectionIllustrationResponseDto;
import com.storycraft.illustration.entity.Illustration;
import com.storycraft.illustration.repository.IllustrationRepository;
import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.story.entity.Story;
import com.storycraft.story.entity.StorySection;
import com.storycraft.story.repository.StoryRepository;
import com.storycraft.story.repository.StorySectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IllustrationService {

    private static final int GROUP_SIZE = 3;

    private final IllustrationRepository illustrationRepository;
    private final StoryRepository storyRepository;
    private final AiDalleService aiDalleService;
    private final StorySectionRepository storySectionRepository;

    //단락 3개씩 나눠서 삽화 생성
    public SectionIllustrationResponseDto createSectionIllustrations(Long storyId, ChildProfile child) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 동화입니다."));

        List<IllustrationResponseDto> responses = new ArrayList<>();

        int page = 0;
        while (true) {
            PageRequest pr = PageRequest.of(page, GROUP_SIZE, Sort.by(Sort.Direction.ASC, "orderIndex"));
            Page<StorySection> sectionPage = storySectionRepository.findByStory(story, pr);

            if (sectionPage.isEmpty()) {
                break;
            }

            for (StorySection section : sectionPage.getContent()) {
                int order = section.getOrderIndex();

                if (illustrationRepository.existsByStoryAndOrderIndex(story, order)) {
                    continue;
                }

                String prompt = section.getParagraphText() + "의 동화 내용을 어린이 동화 스타일로 그려줘."; //TODO: 이미지 생성 Prompt 고도화 및 스타일 고정 필요
                String imageUrl = aiDalleService.generateImage(prompt);

                Illustration illustration = illustrationRepository.save(
                        Illustration.builder()
                                .story(story)
                                .orderIndex(order)
                                .imageUrl(imageUrl)
                                .description(section.getParagraphText()) // TODO: GPT 요약 적용
                                .build()
                );
                responses.add(IllustrationResponseDto.from(illustration, section));
            }
            page++;
        }
        return SectionIllustrationResponseDto.builder()
                .storyId(storyId)
                .illustrations(responses)
                .build();
    }

    //한번에 단락별 삽화 생성
    public SectionIllustrationResponseDto createAllSectionIllustrations(Long storyId, ChildProfile child) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 동화입니다."));

        List<StorySection> sections = storySectionRepository.findAllByStoryOrderByOrderIndex(story);

        List<IllustrationResponseDto> responses = new ArrayList<>();

        for (StorySection section : sections) {
            String prompt = section.getParagraphText() + "의 동화 내용을 어린이 동화 스타일로 그려줘."; //TODO: 이미지 생성 Prompt 고도화 및 스타일 고정 필요
            String imageUrl = aiDalleService.generateImage(prompt);

            Illustration illustration = illustrationRepository.save(Illustration.builder()
                    .story(story)
                    .orderIndex(section.getOrderIndex())
                    .imageUrl(imageUrl)
                    .description(section.getParagraphText())                                //TODO: GPT로 단락 요약해서 넣기
                    .build());

            responses.add(IllustrationResponseDto.from(illustration, section));
        }
        return SectionIllustrationResponseDto.builder()
                .storyId(storyId)
                .illustrations(responses)
                .build();
    }

    // 삽화 상세 조회
    public IllustrationResponseDto getIllustration(Long id, ChildProfile child) {
        Illustration illustration = illustrationRepository.findByIdAndStory_ChildId(id, child)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 삽화가 존재하지 않습니다."));

        return illustration.toDto();
    }

    // 삽화 목록 조회
    public List<IllustrationResponseDto> getIllustraitonList(ChildProfile child) {
        return illustrationRepository.findAllByStory_ChildId(child).stream()
                .map(Illustration::toDto)
                .toList();
    }

    // 삽화 삭제
    public void deleteIllustration(Long id, ChildProfile child) {
        Illustration illu = illustrationRepository.findByIdAndStory_ChildId(id, child)
                .orElseThrow(() -> new IllegalArgumentException("삭제하려는 삽화가 존재하지 않거나 권한이 없습니다."));
        illustrationRepository.delete(illu);
    }

    public String getUrlByStoryId(Long storyId) {
        List<Illustration> illustrations = illustrationRepository.findAllByStory_Id(storyId);
        if (illustrations == null || illustrations.isEmpty()) {
            throw new IllegalArgumentException("해당 동화의 삽화가 존재하지 않습니다.");
        }
        return illustrations.get(0).getImageUrl(); // 첫 번째 삽화의 URL 사용 (썸네일 용도)
    }
}
