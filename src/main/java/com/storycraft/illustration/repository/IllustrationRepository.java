package com.storycraft.illustration.repository;

import com.storycraft.illustration.entity.Illustration;
import com.storycraft.story.entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IllustrationRepository extends JpaRepository<Illustration, Long> {
    List<Illustration> findAllByStory(Story story);

    List<Illustration> findAllByStory_StoryId(Long storyId);
}
