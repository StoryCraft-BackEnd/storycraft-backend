package com.storycraft.reward.service;

import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.profile.repository.ChildProfileRepository;
import com.storycraft.reward.entity.DailyMissionStatus;
import com.storycraft.reward.repository.DailyMissionStatusRepository;
import com.storycraft.reward.repository.RewardPointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyMissionUpdateService {
    
    private final ChildProfileRepository childProfileRepository;
    private final DailyMissionStatusRepository dailyMissionStatusRepository;
    private final RewardPointRepository rewardPointRepository;

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
            int targetCount = getTargetCount(missionCode);
            
            // 오늘 진행 상황 계산
            int progressCount = getTodayProgressCount(child, missionCode, todayStart, todayEnd);
            
            // 미션 완료 여부 확인
            boolean completed = progressCount >= targetCount;
            
            // DB에 미션 상태 저장/업데이트
            updateMissionStatus(child, missionCode, progressCount, completed);
            
            log.debug("데일리 미션 상태 업데이트 완료 - childId: {}, missionCode: {}, progressCount: {}, completed: {}", 
                    childId, missionCode, progressCount, completed);
                    
        } catch (Exception e) {
            log.error("데일리 미션 상태 업데이트 실패 - childId: {}, rewardType: {}, error: {}", 
                    childId, rewardType, e.getMessage());
        }
    }

    private String getMissionCodeFromRewardType(String rewardType) {
        return switch (rewardType) {
            case "POINT_STORY_READ" -> "DAILY_STORY_READ";
            case "POINT_WORD_CLICK" -> "DAILY_WORD_CLICK";
            case "POINT_QUIZ_CORRECT" -> "DAILY_QUIZ_PASS";
            default -> null;
        };
    }

    private int getTargetCount(String missionCode) {
        return switch (missionCode) {
            case "DAILY_STORY_READ" -> 1;
            case "DAILY_WORD_CLICK" -> 10;
            case "DAILY_QUIZ_PASS" -> 10;
            default -> 0;
        };
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
            log.warn("UNIQUE 제약 조건 위반으로 재시도 - childId: {}, missionCode: {}", child.getId(), missionCode);
            Optional<DailyMissionStatus> existingStatus = dailyMissionStatusRepository.findByChildAndMissionCode(child, missionCode);
            if (existingStatus.isPresent()) {
                DailyMissionStatus missionStatus = existingStatus.get();
                missionStatus.setProgressCount(progressCount);
                missionStatus.setCompleted(completed);
                dailyMissionStatusRepository.save(missionStatus);
            }
        }
    }
}
