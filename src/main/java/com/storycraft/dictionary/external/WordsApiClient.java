package com.storycraft.dictionary.external;

import com.storycraft.dictionary.entity.DictionaryWords;
import org.springframework.stereotype.Component;

@Component
public class WordsApiClient {

    /**
     * 추후 WordsApi 연동하고 수정 예정
     */
    public DictionaryWords fetchWord(String word) {
        return DictionaryWords.builder()
                .word(word)
                .meaning("뜻 : " + word)
                .example("예문 : ")
                .build();
    }
}
