package com.storycraft.reward.service;

import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.profile.repository.ChildProfileRepository;
import com.storycraft.reward.dto.DailyMissionStatusDto;
import com.storycraft.reward.entity.DailyMissionStatus;
import com.storycraft.reward.repository.DailyMissionStatusRepository;
import com.storycraft.reward.repository.RewardPointRepository;
import com.storycraft.reward.dto.DailyMissionCheckResponseDto;
import com.storycraft.reward.dto.RewardPointGrantRequestDto;
import com.storycraft.reward.dto.RewardPointGrantResponseDto;
import com.storycraft.reward.service.RewardPointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DailyMissionService {
    private final DailyMissionStatusRepository dailyMissionStatusRepository;
    private final RewardPointRepository rewardPointRepository;
    private final ChildProfileRepository childProfileRepository;
    private final RewardPointService rewardPointService;

    // 데일리 미션 정의
    private static final List<MissionDefinition> MISSION_DEFINITIONS = List.of(
        new MissionDefinition("DAILY_STORY_READ", "동화 1편 읽기", 1),
        new MissionDefinition("DAILY_WORD_CLICK", "단어 10개 클릭", 10),
        new MissionDefinition("DAILY_QUIZ_PASS", "퀴즈 10개 정답", 10)
    );

    public static class MissionDefinition {
        private final String missionCode;
        private final String description;
        private final int targetCount;

        public MissionDefinition(String missionCode, String description, int targetCount) {
            this.missionCode = missionCode;
            this.description = description;
            this.targetCount = targetCount;
        }

        public String getMissionCode() { return missionCode; }
        public String getDescription() { return description; }
        public int getTargetCount() { return targetCount; }
    }

    @Transactional(readOnly = true)
    public List<DailyMissionStatusDto> getDailyMissionStatus(Long childId) {
        ChildProfile child = childProfileRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자녀입니다."));

        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.plusDays(1).atStartOfDay().minusNanos(1);

        List<DailyMissionStatusDto> missionStatuses = new ArrayList<>();

        for (MissionDefinition missionDef : MISSION_DEFINITIONS) {
            // 오늘 진행 상황 계산
            int progressCount = getTodayProgressCount(child, missionDef.getMissionCode(), todayStart, todayEnd);
            
            // 미션 완료 여부 확인
            boolean completed = progressCount >= missionDef.getTargetCount();
            
            // DB에 미션 상태 저장/업데이트
            updateMissionStatus(child, missionDef.getMissionCode(), progressCount, completed);
            
            missionStatuses.add(new DailyMissionStatusDto(
                missionDef.getMissionCode(),
                missionDef.getDescription(),
                progressCount,
                completed
            ));
        }

        return missionStatuses;
    }

    private int getTodayProgressCount(ChildProfile child, String missionCode, LocalDateTime todayStart, LocalDateTime todayEnd) {
        return switch (missionCode) {
            case "DAILY_STORY_READ" -> 
                rewardPointRepository.countByChildAndRewardTypeAndCreatedAtBetween(child, "POINT_STORY_READ", todayStart, todayEnd);
            case "DAILY_WORD_CLICK" -> 
                rewardPointRepository.countByChildAndRewardTypeAndCreatedAtBetween(child, "POINT_WORD_CLICK", todayStart, todayEnd);
            case "DAILY_QUIZ_PASS" -> 
                rewardPointRepository.countByChildAndRewardTypeAndCreatedAtBetween(child, "POINT_QUIZ_CORRECT", todayStart, todayEnd);
            default -> 0;
        };
    }

    private void updateMissionStatus(ChildProfile child, String missionCode, int progressCount, boolean completed) {
        try {
            Optional<DailyMissionStatus> existingStatus = dailyMissionStatusRepository.findByChildAndMissionCode(child, missionCode);
            
            DailyMissionStatus missionStatus;
            if (existingStatus.isPresent()) {
                missionStatus = existingStatus.get();
                missionStatus.setProgressCount(progressCount);
                missionStatus.setCompleted(completed);
            } else {
                missionStatus = DailyMissionStatus.builder()
                        .child(child)
                        .missionCode(missionCode)
                        .progressCount(progressCount)
                        .completed(completed)
                        .build();
            }
            
            dailyMissionStatusRepository.save(missionStatus);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // UNIQUE 제약 조건 위반 시 재시도 (동시성 문제 해결)
            Optional<DailyMissionStatus> existingStatus = dailyMissionStatusRepository.findByChildAndMissionCode(child, missionCode);
            if (existingStatus.isPresent()) {
                DailyMissionStatus missionStatus = existingStatus.get();
                missionStatus.setProgressCount(progressCount);
                missionStatus.setCompleted(completed);
                dailyMissionStatusRepository.save(missionStatus);
            }
        }
    }

    /**
     * 데일리 미션 완료 여부 확인 (모든 미션이 완료되었는지)
     */
    public boolean isAllDailyMissionsCompleted(Long childId) {
        List<DailyMissionStatusDto> missionStatuses = getDailyMissionStatus(childId);
        return missionStatuses.stream().allMatch(DailyMissionStatusDto::isCompleted);
    }

    /**
     * 데일리 미션 완료 및 보상 지급 처리
     */
    @Transactional
    public DailyMissionCheckResponseDto checkAndClaimDailyMission(String userEmail, Long childId) {
        // 1. 오늘 데일리 미션 3가지 모두 완료 여부 확인
        List<DailyMissionStatusDto> missionStatuses = getDailyMissionStatus(childId);
        boolean allCompleted = missionStatuses.stream().allMatch(DailyMissionStatusDto::isCompleted);
        if (!allCompleted) {
            return new DailyMissionCheckResponseDto(0, false);
        }

        // 2. 오늘 이미 보상받았는지 확인 (오늘 날짜에 POINT_DAILY_MISSION 지급 기록이 있는지)
        ChildProfile child = childProfileRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자녀입니다."));
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.plusDays(1).atStartOfDay().minusNanos(1);
        int alreadyClaimedCount = rewardPointRepository.countByChildAndRewardTypeAndCreatedAtBetween(child, "POINT_DAILY_MISSION", todayStart, todayEnd);
        if (alreadyClaimedCount > 0) {
            return new DailyMissionCheckResponseDto(0, true);
        }

        // 3. 포인트 지급 (자동 레벨업/배지 포함)
        RewardPointGrantRequestDto pointRequest = new RewardPointGrantRequestDto();
        pointRequest.setChildId(childId);
        pointRequest.setRewardType("POINT_DAILY_MISSION");
        pointRequest.setContext("DAILY_MISSION");
        rewardPointService.grantPoint(userEmail, pointRequest);

        return new DailyMissionCheckResponseDto(100, false);
    }


} 