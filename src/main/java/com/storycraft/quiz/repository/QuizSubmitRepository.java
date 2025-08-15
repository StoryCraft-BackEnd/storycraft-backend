package com.storycraft.quiz.repository;

import com.storycraft.quiz.entity.QuizCreate;
import com.storycraft.quiz.entity.QuizSubmit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface QuizSubmitRepository extends JpaRepository<QuizSubmit, Long> {
    List<QuizSubmit> findByChild_Id(Long childId);

    // Quiz PK 값(Long)으로 검색
    List<QuizSubmit> findByQuizCreate_QuizId(Long quizId);

    void deleteAllByQuizCreate(QuizCreate quizCreate);
    void deleteAllByQuizCreateIn(Collection<QuizCreate> quizCreates);
}
