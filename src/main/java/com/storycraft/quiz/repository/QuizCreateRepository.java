package com.storycraft.quiz.repository;

import com.storycraft.quiz.entity.QuizCreate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizCreateRepository extends JpaRepository<QuizCreate, Long> {
    List<QuizCreate> findAllByStory_StoryId(Long storyID);
}
