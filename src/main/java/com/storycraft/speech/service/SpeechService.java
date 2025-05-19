package com.storycraft.speech.service;

import com.storycraft.speech.dto.TtsCreateResponseDto;
import com.storycraft.speech.entity.Tts;
import com.storycraft.speech.repository.TtsRepository;
import com.storycraft.story.entity.Story;
import com.storycraft.story.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpeechService {

    private final TtsRepository ttsRepository;
    private final StoryRepository storyRepository;

    /**
     * TTS 생성 메소드
     */
    public TtsCreateResponseDto createTts(Long storyID) {
        Story story = storyRepository.findById(storyID)
                .orElseThrow(() -> new IllegalArgumentException("해당 동화가 없습니다.  ID : " + storyID));

        // TODO: 추후 AWS Polly 연동뒤에 수정
        String pollyUrl = "https://dummy-url.com/audio.mp3";

        Tts saved = ttsRepository.save(Tts.builder()
                .story(story)
                .ttsUrl(pollyUrl)
                .build());

        return saved.toDto();
    }

    /**
     * STT 변환 메소드
     */
    public List<String> transcribeStt(MultipartFile file) {

        // TODO: Whisper 연동후 수정 예정
        String whisperTranscript = "The dog is sleeping in the forest.";

        return Arrays.asList(whisperTranscript.split(" "));
    }
}
