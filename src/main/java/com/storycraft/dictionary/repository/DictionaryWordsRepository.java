package com.storycraft.dictionary.repository;

import com.storycraft.dictionary.entity.DictionaryWords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DictionaryWordsRepository extends JpaRepository<DictionaryWords, Long> {
    Optional<DictionaryWords> findByWord(String word);
}
