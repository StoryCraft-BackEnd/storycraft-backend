package com.storycraft.quiz.repository;

import com.storycraft.quiz.entity.QuizCreate;
import com.storycraft.story.entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizCreateRepository extends JpaRepository<QuizCreate, Long> {
    List<QuizCreate> findAllByStory_Id(Long storyId);
    List<QuizCreate> findAllByStoryOrderByQuizIdAsc(Story story);
    List<QuizCreate> findAllByStory(Story story);
    void deleteAllByStory(Story story);
}
