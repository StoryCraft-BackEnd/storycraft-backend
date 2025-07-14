package com.storycraft.story.repository;

import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.story.entity.StoryBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoryBookmarkRepository extends JpaRepository<StoryBookmark, Long> {
    List<StoryBookmark> findAllByChildId(ChildProfile childId);

    boolean existsByStory_StoryIdAndChildId(Long storyId, ChildProfile childId);

    void deleteByStory_StoryIdAndChildId(Long storyId, ChildProfile childId);
}
