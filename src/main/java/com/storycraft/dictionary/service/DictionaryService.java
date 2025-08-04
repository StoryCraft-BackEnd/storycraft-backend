package com.storycraft.dictionary.service;

import com.storycraft.dictionary.dto.SaveWordResponseDto;
import com.storycraft.dictionary.dto.WordResponseDto;
import com.storycraft.dictionary.entity.DictionaryWords;
import com.storycraft.dictionary.entity.SavedWords;
import com.storycraft.dictionary.external.WordsApiClient;
import com.storycraft.dictionary.repository.DictionaryWordsRepository;
import com.storycraft.dictionary.repository.SavedWordsRepository;
import com.storycraft.profile.entity.ChildProfile;
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

    //자녀의 사용자 사전에 단어 저장
    public SaveWordResponseDto savedWord(Long userId,Long childId, String word) {
        ChildProfile child = childProfileRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("자녀 정보를 찾을 수 없습니다."));

        if (!child.getUser().getId().equals(userId)) {
            throw new SecurityException("해당 자녀에 대한 접근 권한이 없습니다.");
        }

        DictionaryWords dictionaryWord = getOrFetchWord(word);

        boolean alreadySaved = savedWordsRepository.existsByChildIdAndWord(child, dictionaryWord);
        if (alreadySaved) {
            return savedWordsRepository.findByChildIdAndWord(child, dictionaryWord)
                    .map(SavedWords::toDto)
                    .orElseThrow(() -> new IllegalStateException("이미 저장된 단어가 유실되었습니다."));
        }
        SavedWords saved = SavedWords.builder()
                .childId(child)
                .word(dictionaryWord)
                .build();

        return savedWordsRepository.save(saved).toDto();
    }

    //단어 조회 응답용 DTO 반환
    public WordResponseDto getWord(String word) {
        DictionaryWords dictionaryWords = getOrFetchWord(word);
        return dictionaryWords.toDto();
    }

    public List<SaveWordResponseDto> getSavedWords(Long userId, Long childId) {
        ChildProfile child = childProfileRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("자녀 정보를 찾을 수 없습니다."));
        if (!child.getUser().getId().equals(userId)) {
            throw new SecurityException("해당 자녀에 대한 접근 권한이 없습니다.");
        }
        return savedWordsRepository.findByChildId(child).stream()
                .map(SavedWords::toDto)
                .collect(Collectors.toList());
    }

    public void deleteSavedWord(Long userId, Long savedId) {
        SavedWords saved = savedWordsRepository.findById(savedId)
                .orElseThrow(() -> new IllegalArgumentException("저장된 단어를 찾을 수 없습니다."));

        if (!saved.getChildId().getUser().getId().equals(userId)) {
            throw new SecurityException("해당 단어에 대한 삭제 권한이 없습니다.");
        }

        savedWordsRepository.delete(saved);
    }


}
