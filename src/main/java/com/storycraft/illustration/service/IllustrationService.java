package com.storycraft.illustration.service;


import com.storycraft.ai.service.AiDalleService;
import com.storycraft.illustration.dto.IllustrationRequestDto;
import com.storycraft.illustration.dto.IllustrationResponseDto;
import com.storycraft.illustration.entity.Illustration;
import com.storycraft.illustration.repository.IllustrationRepository;
import com.storycraft.story.entity.Story;
import com.storycraft.story.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IllustrationService {

    private final IllustrationRepository illustrationRepository;
    private final StoryRepository storyRepository;
    private final AiDalleService aiDalleService;
    private final StorySectionRepository storySectionRepository;

    // 삽화(썸네일) 생성
    public IllustrationResponseDto createIllustration(IllustrationRequestDto dto) {
        Story story = storyRepository.findById(dto.getStoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 storyId입니다."));

        String prompt = "이 동화의 썸네일 삽화를 그려줘 \n" + story.getContent()+"cartoon풍의 귀여운 느낌으로!";
        //+ "\n \n처음 고른 삽화 스타일 대로."; -> TODO:스타일 추가 후 수정

        String imageUrl = aiDalleService.generateImage(prompt);

        Illustration illustration = Illustration.builder()
                .story(story)
                .imageUrl(imageUrl)
                .description(dto.getPrompt())
                .build();

        Illustration saved = illustrationRepository.save(illustration);
        return saved.toDto();

    }

    // 삽화 상세 조회
    public IllustrationResponseDto getIllustration(Long id) {
        Illustration illustration = illustrationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 삽화가 존재하지 않습니다."));

        return illustration.toDto();
    }

    // 삽화 목록 조회
    public List<IllustrationResponseDto> getIllustraitonList() {
        return illustrationRepository.findAll().stream()
                .map(Illustration::toDto)
                .toList();
    }

    // 삽화 삭제
    public void deleteIllustration(Long id) {
        if (!illustrationRepository.existsById(id)) {
            throw new IllegalArgumentException("삭제하려는 삽화가 존재하지 않습니다.");
        }
        illustrationRepository.deleteById(id);
    }

    public String getUrlByStoryId(Long storyId) {
        List<Illustration> illustrations = illustrationRepository.findAllByStory_StoryId(storyId);
        if (illustrations == null || illustrations.isEmpty()) {
            throw new IllegalArgumentException("해당 동화의 삽화가 존재하지 않습니다.");
        }
        return illustrations.get(0).getImageUrl(); // 첫 번째 삽화의 URL 사용 (썸네일 용도)
    }
}
