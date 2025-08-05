package com.storycraft.story.repository;

import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.story.entity.Story;
import com.storycraft.story.entity.StoryProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoryProgressRepository extends JpaRepository<StoryProgress, Long> {
    Optional<StoryProgress> findByChildAndStory(ChildProfile child, Story story);

    List<StoryProgress> findAllByChild(ChildProfile child);

    Optional<StoryProgress> findByStory_IdAndChild_Id(Long storyId, Long childId);
}
