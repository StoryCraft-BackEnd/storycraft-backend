package com.storycraft.dictionary.service;

import com.storycraft.dictionary.dto.SaveWordResponseDto;
import com.storycraft.dictionary.dto.WordResponseDto;
import com.storycraft.dictionary.entity.DictionaryWords;
import com.storycraft.dictionary.entity.SavedWords;
import com.storycraft.dictionary.external.WordsApiClient;
import com.storycraft.dictionary.repository.DictionaryWordsRepository;
import com.storycraft.dictionary.repository.SavedWordsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DictionaryService {

    private final DictionaryWordsRepository dictionaryWordsRepository;
    private final SavedWordsRepository savedWordsRepository;
    private final WordsApiClient wordsApiClient;


    /**
     * 단어 뜻/예문 조회 (DB에 없을 경우 WordsAPI에서 가져와 저장)
     */
    public DictionaryWords getOrFetchWord(String word) {
        return dictionaryWordsRepository.findByWord(word)
                .orElseGet(() -> {
                    DictionaryWords fetched = wordsApiClient.fetchWord(word);
                    return dictionaryWordsRepository.save(fetched);
                });
    }

    /**
     * 단어 조회 응답용 DTO 반환
     */
    public WordResponseDto getWord(String word) {
        DictionaryWords dictionaryWords = getOrFetchWord(word);
        return dictionaryWords.toDto();
    }

    /**
     * 자녀의 사용자 사전에 단어 저장
     */
    public SaveWordResponseDto savedWord(String childId, String word) {
        DictionaryWords dictionaryWords = getOrFetchWord(word);     //항상 저장할 수 있도록 보장

        boolean alreadySaved = savedWordsRepository.existsByChildIdAndWord(childId, dictionaryWords);
        if (alreadySaved) {
            return savedWordsRepository.findByChildIdAndWord(childId, dictionaryWords)
                    .map(SavedWords::toDto)
                    .orElseThrow(() -> new IllegalStateException("이미 저장된 단어 정보를 찾을 수 없습니다."));
        }

        SavedWords saved = SavedWords.builder()
                .childId(childId)
                .word(dictionaryWords)
                .build();

        return savedWordsRepository.save(saved).toDto();
    }
}
