package com.storycraft.reward.service;

import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.profile.repository.ChildProfileRepository;
import com.storycraft.reward.dto.RewardPointGrantRequestDto;
import com.storycraft.reward.dto.RewardPointGrantResponseDto;
import com.storycraft.reward.entity.RewardPoint;
import com.storycraft.reward.repository.RewardPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RewardPointService {
    private final RewardPointRepository rewardPointRepository;
    private final ChildProfileRepository childProfileRepository;

    @Transactional
    public RewardPointGrantResponseDto grantPoint(String userEmail, RewardPointGrantRequestDto request) {
        // childId는 request에서 받거나, userEmail로부터 추출(보호자-자녀 구조에 따라 확장 가능)
        if (request.getChildId() == null) {
            throw new IllegalArgumentException("childId는 필수입니다.");
        }
        ChildProfile child = childProfileRepository.findById(request.getChildId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자녀입니다."));
        // TODO: userEmail과 child.user.email 일치 여부 체크(보안)

        // 포인트 지급
        int points = getPointValue(request.getRewardType());
        if (points == 0) {
            throw new IllegalArgumentException("유효하지 않은 rewardType입니다: " + request.getRewardType());
        }

        RewardPoint rewardPoint = RewardPoint.builder()
                .child(child)
                .rewardType(request.getRewardType())
                .context(request.getContext())
                .points(points)
                .build();
        rewardPointRepository.save(rewardPoint);

        int totalPoint = rewardPointRepository.sumPointsByChild(child);
        return new RewardPointGrantResponseDto(rewardPoint.getPoints(), totalPoint);
    }

    private int getPointValue(String rewardType) {
        return switch (rewardType) {
            case "POINT_STORY_READ" -> 30;
            case "POINT_WORD_CLICK" -> 5;
            case "POINT_QUIZ_CORRECT" -> 10;
            case "POINT_DAILY_MISSION" -> 100;
            case "POINT_STREAK_3" -> 50;
            case "POINT_STREAK_7" -> 100;
            case "POINT_STREAK_14" -> 200;
            default -> 0;
        };
    }

    /**
     * 유효한 rewardType 목록 반환 (문서화/검증용)
     */
    public static String[] getValidRewardTypes() {
        return new String[]{
                "POINT_STORY_READ",      // 동화 1편 읽기 완료 (+30pt)
                "POINT_WORD_CLICK",      // 단어 1회 클릭 (+5pt)
                "POINT_QUIZ_CORRECT",    // 퀴즈 1문제 정답 (+10pt)
                "POINT_DAILY_MISSION",   // 데일리 미션 전체 달성 (+100pt)
                "POINT_STREAK_3",        // 3일 연속 학습 (+50pt)
                "POINT_STREAK_7",        // 7일 연속 학습 (+100pt)
                "POINT_STREAK_14"        // 14일 연속 학습 (+200pt)
        };
    }
} 