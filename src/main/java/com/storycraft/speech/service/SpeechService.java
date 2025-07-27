package com.storycraft.speech.service;

import com.storycraft.ai.dto.StoryContentDto;
import com.storycraft.ai.service.AiGptService;
import com.storycraft.ai.service.AiWhisperService;
import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.speech.dto.SttResponseDto;
import com.storycraft.speech.dto.TtsCreateResponseDto;
import com.storycraft.speech.entity.Tts;
import com.storycraft.speech.repository.TtsRepository;
import com.storycraft.story.dto.StoryResponseDto;
import com.storycraft.story.entity.Story;
import com.storycraft.story.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpeechService {

    private final TtsRepository ttsRepository;
    private final StoryRepository storyRepository;
    private final AiWhisperService aiWhisperService;
    private final AiGptService aiGptService;

    /**
     * TTS 생성 메소드
     */
    public TtsCreateResponseDto createTts(TtsCreateRequestDto dto) {
        StorySection section = storySectionRepository.findById(dto.getSectionId())
                .orElseThrow(() -> new IllegalArgumentException("해당 단락이 존재하지 않습니다."));

        Story story = section.getStory();

        String text;
        String language;

        if ("Seoyeon".equalsIgnoreCase(dto.getVoiceId()) || "Joon".equalsIgnoreCase(dto.getVoiceId())) {
            text = section.getParagraphTextKr();
            language = "ko";
        } else {
            text = section.getParagraphText();
            language = "en";
        }

        String ttsUrl = pollyService.synthesizeTtsToS3(text, dto.getVoiceId(), dto.getSpeechRate(), "tts");

        Tts saved = ttsRepository.save(Tts.builder()
                .story(story)
                .ttsUrl(pollyUrl)
                .build());

        return saved.toDto();
    }

    /**
     * STT 변환 메소드
     */
    public SttResponseDto transcribeStt(MultipartFile audioFile) {
        try {
            File tempFile = File.createTempFile("audio", ".mp3");
            try {
                audioFile.transferTo(tempFile);
                String transcript = aiWhisperService.transcribeAudio(tempFile);
                List<String> keywords = Arrays.stream(transcript.split("\\s+"))
                        .filter(word -> word.length() > 1) // 한 글자 제거 등 전처리
                        .toList();
                return SttResponseDto.builder().keywords(keywords).build();
            } finally {
                tempFile.delete(); // 임시 파일 삭제
            }
        } catch (Exception e) {
            throw new RuntimeException("STT 실패: " + e.getMessage());
        }
    }

    /**
     * STT 기반 동화 생성 메소드
     */
    public StoryResponseDto generateStoryFromStt(MultipartFile file, ChildProfile childId) {
        try {
            File tempFile = File.createTempFile("audio", ".mp3");
            try {
                file.transferTo(tempFile);

                // 1. Whisper STT
                String transcript = aiWhisperService.transcribeAudio(tempFile);
                List<String> keywords = Arrays.stream(transcript.split("\\s+"))
                        .filter(word -> word.length() > 1)
                        .toList();

                // 2. GPT 생성
                StoryContentDto storyContent = aiGptService.generateStoryContent(keywords);

                String title = storyContent.getTitle();
                String content = storyContent.getContent();

                // 3. 저장
                Story story = Story.builder()
                        .title(title)
                        .content(content)
                        .childId(childId)
                        .build();

                Story saved = storyRepository.save(story);
                return StoryResponseDto.fromEntity(saved);

            } finally {
                tempFile.delete();
            }

        } catch (Exception e) {
            throw new RuntimeException("STT 기반 동화 생성 실패: " + e.getMessage());
        }
    }

    public String getTtsUrlByStoryId(Long storyId) {
        Tts tts = ttsRepository.findByStory_Id(storyId)
                .orElseThrow(() -> new IllegalArgumentException("TTS가 생성되지 않은 동화입니다."));
        return tts.getTtsUrl();
    }
}
