package com.storycraft.speech.repository;

import com.storycraft.speech.entity.Tts;
import com.storycraft.story.entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TtsRepository extends JpaRepository<Tts, Long> {
    List<Tts> findAllByStory(Story story);

    Optional<Tts> findByStory_Id(Long storyId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from Tts t where t.story = :story")
    void deleteAllByStory(@Param("story") Story story);
}
