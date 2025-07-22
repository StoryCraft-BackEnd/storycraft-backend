package com.storycraft.speech.repository;

import com.storycraft.speech.entity.Tts;
import com.storycraft.story.entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TtsRepository extends JpaRepository<Tts, Long> {
    List<Tts> findAllByStory(Story story);

    Optional<Tts> findByStory_Id(Long storyId);
}
