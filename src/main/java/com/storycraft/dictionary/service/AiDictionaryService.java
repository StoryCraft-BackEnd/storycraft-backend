package com.storycraft.dictionary.service;

import com.storycraft.ai.service.AiGptService;
import com.storycraft.dictionary.entity.DictionaryWords;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiDictionaryService {

    private final AiGptService aiGptService;

    public DictionaryWords fetchWordWithGpt(String word) {
        String prompt = String.format("""
                다음 단어를 설명해줘 : "%s"
                
                출력 형식:
                뜻: [한국어 뜻]
                예문(영어): [영어 예문, 초등학생이 이해할 수 있게]
                예문(한글): [영어 예문에 대한 해석]
                
                반드시 위 형식 그대로 출력해줘. 설명 문장 없이.
                """, word);

        String system = "너는 유아를 위한 단어 뜻과 예문을 잘 만들어주는 AI야.";

        String gptAnswer = aiGptService.sendPrompt(prompt, system, 0.7);

        String meaning = extractValue(gptAnswer, "뜻:");
        String exampleEng = extractValue(gptAnswer, "예문(영어):");
        String exampleKor = extractValue(gptAnswer, "예문(한글):");

        return DictionaryWords.builder()
                .word(word)
                .meaning(meaning)
                .exampleEng(exampleEng)
                .exampleKor(exampleKor)
                .build();
    }

    public List<DictionaryWords> fetchWordsWithGpt(Set<String> words) {
        return words.stream()
                .map(this::fetchWordWithGpt)
                .toList();
    }

    private String extractValue(String text, String key) {
        return Arrays.stream(text.split("\n"))
                .filter(line -> line.startsWith(key))
                .map(line -> line.replace(key, "").trim())
                .findFirst()
                .orElse("정보 없음");
    }
}
