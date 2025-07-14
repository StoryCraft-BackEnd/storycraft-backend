package com.storycraft.quiz.repository;

import com.storycraft.quiz.entity.QuizSubmit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizSubmitRepository extends JpaRepository<QuizSubmit, Long> {
    List<QuizSubmit> findByChildId(ChildProfile childId);

    List<QuizSubmit> findByQuizCreate_QuizId(Long quizId);
}
