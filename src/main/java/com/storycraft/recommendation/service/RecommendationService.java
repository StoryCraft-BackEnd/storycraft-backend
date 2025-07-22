package com.storycraft.recommendation.service;

import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.recommendation.dto.RecommendationFeedbackRequestDto;
import com.storycraft.recommendation.dto.RecommendResponseDto;
import com.storycraft.recommendation.entity.StoryRecommendationFeedback;
import com.storycraft.recommendation.repository.RecommendationRepository;
import com.storycraft.story.entity.Story;
import com.storycraft.story.repository.StoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final StoryRepository storyRepository;

    /**
     * 자녀 ID 기준 추천 동화 리스트 조회 (자기 동화 제외)
     */
    public List<RecommendResponseDto> getRecommendations(ChildProfile childId) {
        List<Story> stories = storyRepository.findTop10ByChildIdNot(childId);

        return stories.stream()
                .map(story -> StoryRecommendationFeedback.builder()
                        .childId(childId)
                        .story(story)
                        .build()
                        .toDto()
                ).collect(Collectors.toList());
    }

    /**
     * 추천 피드백 저장 (중복이면 업데이트, 없으면 insert)
     */
    @Transactional
    public void saveOrUpdateFeedback(RecommendationFeedbackRequestDto dto) {
        Story story = storyRepository.findById(dto.getStoryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 동화를 찾을 수 없습니다."));

        recommendationRepository.findByChildIdAndStory_Id(dto.getChildId(), dto.getStoryId())
                .ifPresentOrElse(
                        feedback -> {
                            feedback.setLiked(dto.getLiked());
                            feedback.setRead(dto.getRead());
                        },
                        () -> {
                            StoryRecommendationFeedback newFeedback = StoryRecommendationFeedback.builder()
                                    .childId(dto.getChildId())
                                    .story(story)
                                    .liked(dto.getLiked())
                                    .read(dto.getRead())
                                    .build();
                            recommendationRepository.save(newFeedback);
                        }
                );
    }
}
