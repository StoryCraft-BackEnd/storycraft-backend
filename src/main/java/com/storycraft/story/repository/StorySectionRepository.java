package com.storycraft.story.repository;

import com.storycraft.story.entity.Story;
import com.storycraft.story.entity.StorySection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StorySectionRepository extends JpaRepository<StorySection, Long> {
    List<StorySection> findAllByStoryOrderByOrderIndex(Story story);
}
