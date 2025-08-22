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

    /**
     * 포인트 지급 시 데일리 미션 상태 자동 업데이트
     * @param childId 자녀 ID
     * @param rewardType 포인트 타입 (POINT_STORY_READ, POINT_WORD_CLICK, POINT_QUIZ_CORRECT)
     */
    @Transactional
    public void updateDailyMissionStatusOnPointGrant(Long childId, String rewardType) {
        try {
            ChildProfile child = childProfileRepository.findById(childId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자녀입니다."));

            LocalDate today = LocalDate.now();
            LocalDateTime todayStart = today.atStartOfDay();
            LocalDateTime todayEnd = today.plusDays(1).atStartOfDay().minusNanos(1);

            // 포인트 타입에 따른 미션 코드 매핑
            String missionCode = getMissionCodeFromRewardType(rewardType);
            if (missionCode == null) {
                return; // 데일리 미션과 관련 없는 포인트 타입
            }

            // 해당 미션의 목표 횟수 찾기
            MissionDefinition missionDef = MISSION_DEFINITIONS.stream()
                    .filter(def -> def.getMissionCode().equals(missionCode))
                    .findFirst()
                    .orElse(null);
            
            if (missionDef == null) {
                return;
            }

            // 오늘 진행 상황 계산
            int progressCount = getTodayProgressCount(child, missionCode, todayStart, todayEnd);
            
            // 미션 완료 여부 확인
            boolean completed = progressCount >= missionDef.getTargetCount();
            
            // DB에 미션 상태 저장/업데이트
            updateMissionStatus(child, missionCode, progressCount, completed);
            
            // log.info("데일리 미션 상태 업데이트 완료 - childId: {}, missionCode: {}, progressCount: {}, completed: {}", 
            //         childId, missionCode, progressCount, completed);
                    
        } catch (Exception e) {
            // log.error("데일리 미션 상태 업데이트 실패 - childId: {}, rewardType: {}, error: {}", 
            //         childId, rewardType, e.getMessage(), e);
        }
    }

    /**
     * 포인트 타입을 미션 코드로 변환
     */
    private String getMissionCodeFromRewardType(String rewardType) {
        return switch (rewardType) {
            case "POINT_STORY_READ" -> "DAILY_STORY_READ";
            case "POINT_WORD_CLICK" -> "DAILY_WORD_CLICK";
            case "POINT_QUIZ_CORRECT" -> "DAILY_QUIZ_PASS";
            default -> null;
        };
    }
} 