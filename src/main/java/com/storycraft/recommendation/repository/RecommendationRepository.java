package com.storycraft.recommendation.repository;

import com.storycraft.recommendation.entity.StoryRecommendationFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecommendationRepository extends JpaRepository<StoryRecommendationFeedback, Long> {

    // 특정 자녀가 특정 동화에 남긴 피드백 조회 (중복 저장 방지)
    Optional<StoryRecommendationFeedback> findByChildIdAndStory_StoryId(ChildProfile childId, Long storyId);

    // 자녀가 남긴 모든 피드백 조회
    List<StoryRecommendationFeedback> findAllByChildId(ChildProfile childId);

    // 동화별 남겨진 모든 피드백 조회
    List<StoryRecommendationFeedback> findAllByStory_StoryId(Long storyId);

    // 피드백 존재 여부 확인
    boolean existsByChildIdAndStory_StoryId(ChildProfile childId, Long storyId);
}