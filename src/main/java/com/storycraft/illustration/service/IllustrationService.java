package com.storycraft.illustration.service;


import com.storycraft.ai.service.AiDalleService;
import com.storycraft.illustration.dto.IllustrationRequestDto;
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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IllustrationService {

    private final IllustrationRepository illustrationRepository;
    private final StoryRepository storyRepository;
    private final AiDalleService aiDalleService;
    private final StorySectionRepository storySectionRepository;

/*    // 삽화(썸네일) 생성
    public IllustrationResponseDto createIllustration(IllustrationRequestDto dto) {
        Story story = storyRepository.findById(dto.getStoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 storyId입니다."));

        List<String> keywords = story.getKeywords();

        String prompt = "이 동화의 썸네일 삽화를 이 키워드들을 바탕으로 그려줘: "
                + String.join(", ", keywords)
                +" (어린이 동화 스타일로)";
        //+ "\n \n처음 고른 삽화 스타일 대로."; -> TODO:스타일 추가 후 수정

        String imageUrl = aiDalleService.generateImage(prompt);

        int nextOrderIndex = illustrationRepository.findMaxOrderIndexByStory(story).orElse(-1) + 1;

        Illustration illustration = Illustration.builder()
                .story(story)
                .imageUrl(imageUrl)
                .description("(" + String.join(", ", keywords) + ")")
                .build();

        Illustration saved = illustrationRepository.save(illustration);
        return saved.toDto();
    }*/

    //단락별 삽화 생성
    public SectionIllustrationResponseDto createSectionIllustrations(Long storyId, ChildProfile child) {
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
