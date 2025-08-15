package com.storycraft.story.repository;

import com.storycraft.story.entity.Story;
import com.storycraft.story.entity.StorySection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StorySectionRepository extends JpaRepository<StorySection, Long> {
    List<StorySection> findAllByStoryOrderByOrderIndex(Story story);

    Page<StorySection> findByStory(Story story, Pageable pageable);

    @Transactional
    @Modifying
    @Query("DELETE FROM StorySection s WHERE s.story = :story")
    void deleteAllByStory(@Param("story") Story story);
}
