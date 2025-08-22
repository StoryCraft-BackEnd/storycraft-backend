package com.storycraft.profile.service;

import com.storycraft.global.exception.CustomException;
import com.storycraft.global.exception.ErrorCode;
import com.storycraft.profile.dto.ChildProfileCreateRequestDto;
import com.storycraft.profile.dto.ChildProfileIdResponseDto;
import com.storycraft.profile.dto.ChildProfileResponseDto;
import com.storycraft.profile.dto.ChildProfileUpdateRequestDto;
import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.profile.repository.ChildProfileRepository;
import com.storycraft.reward.entity.DailyMissionStatus;
import com.storycraft.reward.entity.StreakStatus;
import com.storycraft.reward.repository.DailyMissionStatusRepository;
import com.storycraft.reward.repository.StreakStatusRepository;
import com.storycraft.user.entity.User;
import com.storycraft.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChildProfileService {

    private final ChildProfileRepository childProfileRepository;
    private final UserRepository userRepository;
    private final DailyMissionStatusRepository dailyMissionStatusRepository;
    private final StreakStatusRepository streakStatusRepository;

    @Transactional
    public ChildProfileIdResponseDto createChildProfile(String email, ChildProfileCreateRequestDto request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ChildProfile.LearningLevel level;
        try {
            level = ChildProfile.LearningLevel.valueOf(request.getLearningLevel());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_LEARNING_LEVEL);
        }

        ChildProfile child = ChildProfile.builder()
                .name(request.getName())
                .age(request.getAge())
                .learningLevel(level)
                .user(user)
                .build();

        childProfileRepository.save(child);
        
        // 새 프로필 생성 시 rewards 관련 기본 데이터 생성
        initializeRewardsData(child);
        
        return new ChildProfileIdResponseDto(child.getId());
    }

    /**
     * 새 자녀 프로필에 대한 rewards 관련 기본 데이터를 초기화합니다.
     */
    private void initializeRewardsData(ChildProfile child) {
        // 1. 데일리 미션 상태 초기화
        initializeDailyMissionStatus(child);
        
        // 2. 연속 학습 상태 초기화
        initializeStreakStatus(child);
    }

    /**
     * 데일리 미션 상태를 초기화합니다.
     */
    private void initializeDailyMissionStatus(ChildProfile child) {
        String[] missionCodes = {"DAILY_STORY_READ", "DAILY_WORD_CLICK", "DAILY_QUIZ_PASS"};
        
        for (String missionCode : missionCodes) {
            DailyMissionStatus missionStatus = DailyMissionStatus.builder()
                    .child(child)
                    .missionCode(missionCode)
                    .progressCount(0)
                    .completed(false)
                    .build();
            
            dailyMissionStatusRepository.save(missionStatus);
        }
    }

    /**
     * 연속 학습 상태를 초기화합니다.
     */
    private void initializeStreakStatus(ChildProfile child) {
        StreakStatus streakStatus = StreakStatus.builder()
                .child(child)
                .currentStreak(0)
                .lastLearnedDate(null)
                .build();
        
        streakStatusRepository.save(streakStatus);
    }

    @Transactional(readOnly = true)
    public List<ChildProfileResponseDto> getChildProfiles(String email) {
        List<ChildProfile> children = childProfileRepository.findByUserEmail(email);
        return children.stream()
                .map(child -> new ChildProfileResponseDto(
                        child.getId(),
                        child.getName(),
                        child.getAge(),
                        child.getLearningLevel().name(),
                        child.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ChildProfileResponseDto getChildProfile(String email, Long childId) {
        ChildProfile child = childProfileRepository.findById(childId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_PROFILE_NOT_FOUND));

        if (!child.getUser().getEmail().equals(email)) {
            throw new CustomException(ErrorCode.CHILD_PROFILE_ACCESS_DENIED);
        }

        return new ChildProfileResponseDto(
                child.getId(),
                child.getName(),
                child.getAge(),
                child.getLearningLevel().name(),
                child.getCreatedAt()
        );
    }

    /**
     * 유저 ID와 자녀 ID로 자녀 프로필을 검증하고 반환
     * @param userId 유저 ID
     * @param childId 자녀 ID
     * @return 검증된 자녀 프로필
     */
    @Transactional(readOnly = true)
    public ChildProfile findByIdAndUserId(Long childId, Long userId) {
        ChildProfile child = childProfileRepository.findById(childId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_PROFILE_NOT_FOUND));

        if (!child.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.CHILD_PROFILE_ACCESS_DENIED);
        }

        return child;
    }

    @Transactional
    public ChildProfileIdResponseDto updateChildProfile(String email, Long childId, ChildProfileUpdateRequestDto request) {
        ChildProfile child = childProfileRepository.findById(childId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_PROFILE_NOT_FOUND));

        if (!child.getUser().getEmail().equals(email)) {
            throw new CustomException(ErrorCode.CHILD_PROFILE_ACCESS_DENIED);
        }

        ChildProfile.LearningLevel level;
        try {
            level = ChildProfile.LearningLevel.valueOf(request.getLearningLevel());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_LEARNING_LEVEL);
        }

        child.setName(request.getName());
        child.setAge(request.getAge());
        child.setLearningLevel(level);

        childProfileRepository.save(child);

        return new ChildProfileIdResponseDto(child.getId());
    }

    @Transactional
    public ChildProfileIdResponseDto deleteChildProfile(String email, Long childId) {
        ChildProfile child = childProfileRepository.findById(childId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_PROFILE_NOT_FOUND));

        if (!child.getUser().getEmail().equals(email)) {
            throw new CustomException(ErrorCode.CHILD_PROFILE_ACCESS_DENIED);
        }

        childProfileRepository.delete(child);
        return new ChildProfileIdResponseDto(childId);
    }
}
