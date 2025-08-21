package com.storycraft.illustration.service;


import com.storycraft.ai.service.AiDalleService;
import com.storycraft.global.util.S3Uploader;
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
import java.util.Comparator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class IllustrationService {

    private static final int GROUP_SIZE = 3;

    private final IllustrationRepository illustrationRepository;
    private final StoryRepository storyRepository;
    private final AiDalleService aiDalleService;
    private final StorySectionRepository storySectionRepository;
    private final S3Uploader s3Uploader;

    private final ThreadPoolTaskExecutor illustrationExecutor;

    //단락 3개씩 나눠서 삽화 생성
    public SectionIllustrationResponseDto createSectionIllustrations(Long storyId, ChildProfile child) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 동화입니다."));

        List<IllustrationResponseDto> allResponses = new ArrayList<>();

        int page = 0;
        while (true) {
            PageRequest pr = PageRequest.of(page, GROUP_SIZE, Sort.by(Sort.Direction.ASC, "orderIndex"));
            Page<StorySection> sectionPage = storySectionRepository.findByStory(story, pr);

            if (sectionPage.isEmpty()) {
                break;
            }

// 이 페이지의 섹션들을 병렬 처리
            List<CompletableFuture<IllustrationResponseDto>> futures = sectionPage.getContent().stream()
                    .map(section -> CompletableFuture.supplyAsync(() -> processOneSection(story, section), illustrationExecutor))
                    .toList();

            // 완료 대기 후 유효한 결과만 수집
            futures.forEach(f -> {
                try {
                    IllustrationResponseDto dto = f.join();
                    if (dto != null) allResponses.add(dto);
                } catch (Exception e) {
                    // join 예외는 이미 내부에서 처리/로깅했으므로 추가 작업 없음
                }
            });
            page++;
        }

        allResponses.sort(Comparator.comparingInt(IllustrationResponseDto::getOrderIndex));

        return SectionIllustrationResponseDto.builder()
                .storyId(storyId)
                .illustrations(allResponses)
                .build();
    }

    /**
     * 한 섹션 처리(이미지 생성 → 업로드 → 저장)
     * - 중복(경합) 발생 시 DB 유니크 제약으로 안전
     * - 실패 시 null 반환하여 전체 흐름은 계속
     */
    private IllustrationResponseDto processOneSection(Story story, StorySection section) {
        final int order = section.getOrderIndex();
        try {
            // 빠른 패스(이미 생성되어 있으면 스킵)
            if (illustrationRepository.existsByStoryAndOrderIndex(story, order)) {
                return null;
            }

            byte[] imageBytes = aiDalleService.generateImage(section.getParagraphText());

            // 스토리 별 디렉토리로 키 구성 → 충돌 최소화/가독성 향상
            String dir = "illustrations/story-" + story.getId();
            String fileName = "section-" + order + ".png";
            String imageUrl = s3Uploader.uploadBytes(imageBytes, dir, fileName);

            // 저장(동시 경합 시 유니크 제약 위반 가능 → 잡아서 멱등 보장)
            Illustration saved = illustrationRepository.save(
                    Illustration.builder()
                            .story(story)
                            .orderIndex(order)
                            .imageUrl(imageUrl)
                            .description(section.getParagraphText()) // TODO: 요약 적용 여지
                            .build()
            );

            return IllustrationResponseDto.from(saved, section);

        } catch (DataIntegrityViolationException dup) {
            // 동시에 같은 섹션이 저장되면 여기로 들어옴 → 멱등: 조용히 스킵
            log.debug("Duplicate illustration ignored (storyId={}, orderIndex={})", story.getId(), order);
            return null;
        } catch (Exception ex) {
            // OpenAI/네트워크/S3 등 단건 실패는 로깅 후 계속 진행
            log.warn("Illustration generation failed (storyId={}, orderIndex={}): {}", story.getId(), order, ex.toString());
            return null;
        }
    }

    /*//한번에 단락별 삽화 생성
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
    }*/

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
