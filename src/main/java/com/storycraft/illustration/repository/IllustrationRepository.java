package com.storycraft.illustration.repository;

import com.storycraft.illustration.entity.Illustration;
import com.storycraft.story.entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IllustrationRepository extends JpaRepository<Illustration, Long> {
    List<Illustration> findAllByStory(Story story);

    List<Illustration> findAllByStory_Id(Long storyId);

    List<Illustration> findAllByStory_ChildId(ChildProfile child);

    Optional<Illustration> findByIdAndStory_ChildId(Long id, ChildProfile child);

    @Query("SELECT MAX(i.orderIndex) FROM Illustration i WHERE i.story = :story")
    Optional<Integer> findMaxOrderIndexByStory(@Param("story") Story story);
}
