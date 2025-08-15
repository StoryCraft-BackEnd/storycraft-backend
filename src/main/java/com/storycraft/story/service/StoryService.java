package com.storycraft.story.service;

import com.storycraft.ai.dto.StoryContentDto;
import com.storycraft.ai.service.AiGptService;
import com.storycraft.global.util.S3Deleter;
import com.storycraft.illustration.entity.Illustration;
import com.storycraft.illustration.repository.IllustrationRepository;
import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.quiz.entity.QuizCreate;
import com.storycraft.quiz.repository.QuizCreateRepository;
import com.storycraft.quiz.repository.QuizSubmitRepository;
import com.storycraft.speech.entity.Tts;
import com.storycraft.speech.repository.TtsRepository;
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

    private final S3Deleter s3Deleter;
    private final StoryRepository storyRepository;
    private final AiGptService aiGptService;
    private final StorySectionService storySectionService;
    private final StorySectionRepository storySectionRepository;
    private final IllustrationRepository illustrationRepository;
    private final TtsRepository ttsRepository;
    private final QuizSubmitRepository quizSubmitRepository;
    private final QuizCreateRepository quizCreateRepository;

    // 동화 생성(child를 Controller에서 검증 후 주입)
    public StoryResponseDto createStory(ChildProfile child, StoryRequestDto dto) {
        String level = String.valueOf(child.getLearningLevel());

        StoryContentDto result = aiGptService.generateStoryContent(dto.getKeywords(), level);

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

    // 동화 상세 조회 (content 소유 제한)
    public StoryResponseDto getStory(Long id, ChildProfile child) {
        Story story = storyRepository.findByIdAndChildId(id, child)
                .orElseThrow(() -> new RuntimeException("동화를 찾을 수 없습니다."));
        return story.toDto();
    }

    // 동화 목록 조회
    public List<StoryResponseDto> getStoryList(ChildProfile childId) {
        return storyRepository.findAllByChildId(childId).stream()
                .map(Story::toDto)
                .collect(Collectors.toList());
    }

    // 동화 수정 (content 소유 제한)
    public StoryResponseDto updateStory(Long id, ChildProfile child, StoryUpdateDto dto) {
        Story story = storyRepository.findByIdAndChildId(id, child)
                .orElseThrow(() -> new RuntimeException("동화를 찾을 수 없습니다."));

        String level = String.valueOf(child.getLearningLevel());

        //새로운 제목/내용 생성
        StoryContentDto updatedStory = aiGptService.regenerateStory(dto.getKeywords(), story.getTitle(), level);

        //동화 업데이트
        story.updateContent(
                updatedStory.getTitle(),
                updatedStory.getContent(),
                updatedStory.getContentKr(),
                dto.getKeywords());

        //기존 단락 삭제
        storySectionRepository.deleteAllByStory(story);

        //새로 생성된 단락 저장
        storySectionService.saveSectionsFromContent(story, updatedStory.getContent(), updatedStory.getContentKr());

        return storyRepository.save(story).toDto();
    }

    // 동화 삭제 (content 소유 제한)
    @Transactional
    public void deleteStory(Long id, ChildProfile child) {
        Story story = storyRepository.findByIdAndChildId(id, child)
                .orElseThrow(() -> new RuntimeException("해당 동화를 찾을 수 없습니다."));

        //1. 삽화 삭제
        List<Illustration> illustrations = illustrationRepository.findAllByStory(story);
        for (Illustration illustration : illustrations) {
            if (illustration.getImageUrl() != null) {
                s3Deleter.deleteFileFromUrl(illustration.getImageUrl());
            }
        }
        illustrationRepository.deleteAll(illustrations);

        //2. TTS 삭제 (S3 → DB)
        List<Tts> ttsList = ttsRepository.findAllByStory(story);
        for (Tts tts : ttsList) {
            String url = tts.getTtsUrl();
            if (url != null && url.isBlank()) {
                try {s3Deleter.deleteFileFromUrl(url);} catch (Exception ignored) {}
            }
        }
        ttsRepository.deleteAllByStory(story);

        //3. 퀴즈 제출 → 퀴즈 생성
        List<QuizCreate> quizCreates = quizCreateRepository.findAllByStory(story);
        if (!quizCreates.isEmpty()) {
            quizSubmitRepository.deleteAllByQuizCreateIn(quizCreates);
        }
        quizCreateRepository.deleteAllByStory(story);

        //4. 동화 단락 삭제
        storySectionRepository.deleteAllByStory(story);

        //5. 동화 삭제
        storyRepository.delete(story);
    }

    // 통합 조회 등을 위한 엔티티 직접 조회용 메서드
    public Story getStoryEntityById(Long id) {
        return storyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("동화를 찾을 수 없습니다."));
    }
}

