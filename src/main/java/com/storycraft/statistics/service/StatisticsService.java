package com.storycraft.statistics.service;

import com.storycraft.dictionary.repository.SavedWordsRepository;
import com.storycraft.global.exception.CustomException;
import com.storycraft.global.exception.ErrorCode;
import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.profile.repository.ChildProfileRepository;
import com.storycraft.quiz.repository.QuizSubmitRepository;
import com.storycraft.statistics.dto.ChildStatisticsResponseDto;
import com.storycraft.story.repository.StoryProgressRepository;
import com.storycraft.story.repository.StoryRepository;
import com.storycraft.user.entity.User;
import com.storycraft.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final ChildProfileRepository childProfileRepository;
    private final UserRepository userRepository;
    private final StoryRepository storyRepository;
    private final StoryProgressRepository storyProgressRepository;
    private final SavedWordsRepository savedWordsRepository;
    private final QuizSubmitRepository quizSubmitRepository;

    @Transactional(readOnly = true)
    public ChildStatisticsResponseDto getChildStatistics(String email, Long childId) {
        // 현재 로그인한 사용자 확인
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 자녀 프로필 조회 및 권한 검증
        ChildProfile child = childProfileRepository.findById(childId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_PROFILE_NOT_FOUND));

        // 현재 로그인한 사용자의 자녀인지 확인
        if (!child.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.CHILD_PROFILE_ACCESS_DENIED);
        }

        // 1. 생성한 동화 수 조회
        Long createdStories = (long) storyRepository.findAllByChildId(child).size();

        // 2. 다 읽은 동화 수 조회 (isRead = true인 동화 수)
        Long completedStories = storyProgressRepository.findAllByChild(child)
                .stream()
                .filter(progress -> progress.isRead())
                .count();

        // 3. 학습한 단어 개수 조회
        Long learnedWords = (long) savedWordsRepository.findByChildId(child).size();

        // 4. 푼 퀴즈 수 조회
        Long solvedQuizzes = (long) quizSubmitRepository.findByChild_Id(child.getId()).size();

        // 5. 총 학습 시간 조회 (분 단위)
        Long totalLearningTimeMinutes = storyProgressRepository.findAllByChild(child)
                .stream()
                .mapToLong(progress -> progress.getLearnedMinutes() + (progress.getLearnedSeconds() / 60))
                .sum();

        return ChildStatisticsResponseDto.builder()
                .createdStories(createdStories)
                .completedStories(completedStories)
                .learnedWords(learnedWords)
                .solvedQuizzes(solvedQuizzes)
                .totalLearningTimeMinutes(totalLearningTimeMinutes)
                .build();
    }
} 