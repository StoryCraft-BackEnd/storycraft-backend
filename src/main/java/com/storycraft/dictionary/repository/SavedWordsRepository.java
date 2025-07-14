package com.storycraft.dictionary.repository;

import com.storycraft.dictionary.entity.DictionaryWords;
import com.storycraft.dictionary.entity.SavedWords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedWordsRepository extends JpaRepository<SavedWords, Long> {

    //단어 사전(Words.API)에서 단어 검색
    List<SavedWords> findByWord(DictionaryWords word);

    boolean existsByChildIdAndWord(ChildProfile childId, DictionaryWords word);

    Optional<SavedWords> findByChildIdAndWord(String childId, DictionaryWords dictionaryWords);
}
