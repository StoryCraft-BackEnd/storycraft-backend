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
        RewardPoint rewardPoint = RewardPoint.builder()
                .child(child)
                .rewardType(request.getRewardType())
                .context(request.getContext())
                .points(getPointValue(request.getRewardType()))
                .build();
        rewardPointRepository.save(rewardPoint);

        int totalPoint = rewardPointRepository.sumPointsByChild(child);
        return new RewardPointGrantResponseDto(rewardPoint.getPoints(), totalPoint);
    }

    private int getPointValue(String rewardType) {
        // TODO: rewardType별 포인트 정책 적용(임시로 30점)
        return 30;
    }
} 