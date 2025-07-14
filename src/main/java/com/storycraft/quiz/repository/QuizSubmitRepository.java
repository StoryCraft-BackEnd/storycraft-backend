package com.storycraft.quiz.repository;

import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.quiz.entity.QuizSubmit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizSubmitRepository extends JpaRepository<QuizSubmit, Long> {
    List<QuizSubmit> findByChildId(ChildProfile childId);

    List<QuizSubmit> findByQuizCreate_QuizId(Long quizId);
}
