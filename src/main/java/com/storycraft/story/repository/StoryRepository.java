package com.storycraft.story.repository;

import com.storycraft.story.entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoryRepository extends JpaRepository<Story, Long> {
    List<Story> findAllByChildId(String childId);

    List<Story> findTop10ByChildIdNot(ChildProfile childId);
}
