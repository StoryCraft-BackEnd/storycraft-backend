package com.storycraft.story.repository;

import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.story.entity.StoryPreview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoryPreviewRepository extends JpaRepository<StoryPreview, Long> {
    List<StoryPreview> findAllByChildIdOrderByCreatedAtDesc(ChildProfile childId);
}
