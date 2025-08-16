package com.storycraft.statistics.service;

import com.storycraft.dictionary.repository.SavedWordsRepository;
import com.storycraft.global.exception.CustomException;
import com.storycraft.global.exception.ErrorCode;
import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.profile.repository.ChildProfileRepository;
import com.storycraft.quiz.repository.QuizSubmitRepository;
import com.storycraft.statistics.dto.ChildStatisticsResponseDto;
import com.storycraft.statistics.dto.TotalLearningTimeRequestDto;
import com.storycraft.statistics.entity.TotalLearningTime;
import com.storycraft.statistics.repository.TotalLearningTimeRepository;
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
    private final TotalLearningTimeRepository totalLearningTimeRepository;

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

        // 5. 총 학습 시간 조회 (분 단위) - 저장된 총 학습 시간 우선 사용
        Long totalLearningTimeMinutes = totalLearningTimeRepository.findByChild(child)
                .map(TotalLearningTime::getTotalLearningTimeMinutes)
                .orElse(0L); // 저장된 시간이 없으면 0분

        return ChildStatisticsResponseDto.builder()
                .createdStories(createdStories)
                .completedStories(completedStories)
                .learnedWords(learnedWords)
                .solvedQuizzes(solvedQuizzes)
                .totalLearningTimeMinutes(totalLearningTimeMinutes)
                .build();
    }

    /**
     * 프론트엔드로부터 받은 총 학습 시간을 저장합니다.
     */
    @Transactional
    public void saveTotalLearningTime(String email, TotalLearningTimeRequestDto requestDto) {
        // 현재 로그인한 사용자 확인
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 자녀 프로필 조회 및 권한 검증
        ChildProfile child = childProfileRepository.findById(requestDto.getChildId())
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_PROFILE_NOT_FOUND));

        // 현재 로그인한 사용자의 자녀인지 확인
        if (!child.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.CHILD_PROFILE_ACCESS_DENIED);
        }

        // 기존 총 학습 시간 기록이 있는지 확인
        TotalLearningTime existingLearningTime = totalLearningTimeRepository.findByChild(child).orElse(null);

        if (existingLearningTime != null) {
            // 기존 기록이 있으면 업데이트
            existingLearningTime.updateLearningTime(
                requestDto.getTotalLearningTimeMinutes(),
                requestDto.getUpdatedAt() != null ? requestDto.getUpdatedAt() : 
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            );
            totalLearningTimeRepository.save(existingLearningTime);
        } else {
            // 새로운 기록 생성
            TotalLearningTime newLearningTime = TotalLearningTime.builder()
                    .child(child)
                    .totalLearningTimeMinutes(requestDto.getTotalLearningTimeMinutes())
                    .lastUpdatedAt(requestDto.getUpdatedAt() != null ? requestDto.getUpdatedAt() : 
                             java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .build();
            totalLearningTimeRepository.save(newLearningTime);
        }
    }
} 