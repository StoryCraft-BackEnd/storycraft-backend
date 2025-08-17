package com.storycraft.dictionary.service;

import com.storycraft.dictionary.dto.SaveWordResponseDto;
import com.storycraft.dictionary.dto.WordResponseDto;
import com.storycraft.dictionary.entity.DictionaryWords;
import com.storycraft.dictionary.entity.SavedWords;
import com.storycraft.dictionary.repository.DictionaryWordsRepository;
import com.storycraft.dictionary.repository.SavedWordsRepository;
import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.profile.repository.ChildProfileRepository;
import com.storycraft.story.entity.Story;
import com.storycraft.story.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DictionaryService {

    private final DictionaryWordsRepository dictionaryWordsRepository;
    private final SavedWordsRepository savedWordsRepository;
    private final ChildProfileRepository childProfileRepository;
    private final AiDictionaryService aiDictionaryService;
    private final StoryRepository storyRepository;


    private void verifyOwnershipOrThrow(Story story, ChildProfile child) {
        Long ownerChildId = story.getChildId().getId();
        if (!ownerChildId.equals(child.getId())) {
            throw new IllegalStateException("요청한 자녀의 컨텐츠가 아닙니다.");
        }
    }

    //단어 뜻/예문 조회 (DB에 없을 경우 GPT로 단어 정보 생성 후 저장)
    public DictionaryWords getOrFetchWord(String word) {
        return dictionaryWordsRepository.findByWord(word)
                .orElseGet(() -> {
                    DictionaryWords fetched = aiDictionaryService.fetchWordWithGpt(word);
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

    //단어 추출 및 저장 메소드
    public List<SaveWordResponseDto> extractWordsAndSave(Long storyId, Long childId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("해당 ID의 동화를 찾을 수 없습니다."));

        ChildProfile child = childProfileRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 자녀를 찾을 수 없습니다."));

        verifyOwnershipOrThrow(story, child);

        String content = story.getContent();

        Set<String> extractedWords = extractWords(content);

        Set<String> newWords = extractedWords.stream()
                .filter(word -> !dictionaryWordsRepository.existsByWord(word))
                .collect(Collectors.toSet());

        List<DictionaryWords> newWordEntities = aiDictionaryService.fetchWordsWithGpt(newWords);

        dictionaryWordsRepository.saveAll(newWordEntities);

        List<DictionaryWords> allWords = dictionaryWordsRepository.findAllByWordIn(extractedWords);

        ChildProfile child = childProfileRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 자녀 프로필을 찾을 수 없습니다."));

        Set<String> alreadySavedWords = savedWordsRepository.findByChildId(child)
                .stream()
                .map(sw -> sw.getWord().getWord())
                .collect(Collectors.toSet());

        List<SavedWords> savedWordEntities = allWords.stream()
                .filter(word -> !alreadySavedWords.contains(word.getWord()))
                .map(word -> SavedWords.builder()
                        .childId(child)
                        .word(word)
                        .build())
                .toList();

        savedWordsRepository.saveAll(savedWordEntities);

        return savedWordEntities.stream()
                .map(SavedWords::toDto)
                .toList();
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

    //단어 추출 메소드
    public Set<String> extractWords(String content) {
        Set<String> wordSet = new HashSet<>();
        Matcher matcher = Pattern.compile("\\*\\*(.*?)\\*\\*").matcher(content);
        while (matcher.find()) {
            String word = matcher.group(1).toLowerCase();
            if (word.length() > 1) wordSet.add(word);
        }
        return wordSet;
    }

    //단어 추출 메소드 오버로딩
    public Set<String> extractWordsByStoryId(Long storyId) {
        String content = storyRepository.findById(storyId)
                .orElseThrow(() -> new IllegalArgumentException("동화를 찾을 수 없습니다."))
                .getContent();
        return extractWords(content);
    }

}
