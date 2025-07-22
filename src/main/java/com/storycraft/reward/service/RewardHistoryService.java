package com.storycraft.reward.service;

import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.profile.repository.ChildProfileRepository;
import com.storycraft.reward.dto.RewardHistoryItemDto;
import com.storycraft.reward.entity.RewardPoint;
import com.storycraft.reward.entity.RewardBadge;
import com.storycraft.reward.repository.RewardPointRepository;
import com.storycraft.reward.repository.RewardBadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardHistoryService {
    private final RewardPointRepository rewardPointRepository;
    private final RewardBadgeRepository rewardBadgeRepository;
    private final ChildProfileRepository childProfileRepository;

    public List<RewardHistoryItemDto> getHistory(Long childId, String type, LocalDate from, LocalDate to) {
        ChildProfile child = childProfileRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자녀입니다."));
        List<RewardHistoryItemDto> result = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.plusDays(1).atStartOfDay().minusNanos(1);
        if (type == null || type.equalsIgnoreCase("point")) {
            List<RewardPoint> points = rewardPointRepository.findAllByChildAndCreatedAtBetween(child, fromDateTime, toDateTime);
            for (RewardPoint p : points) {
                result.add(RewardHistoryItemDto.builder()
                        .date(p.getCreatedAt().toLocalDate().format(dateFormatter))
                        .type("POINT")
                        .rewardType(p.getRewardType())
                        .context(p.getContext())
                        .value(p.getPoints())
                        .build());
            }
        }
        if (type == null || type.equalsIgnoreCase("badge")) {
            List<RewardBadge> badges = rewardBadgeRepository.findAllByChildAndCreatedAtBetween(child, fromDateTime, toDateTime);
            for (RewardBadge b : badges) {
                result.add(RewardHistoryItemDto.builder()
                        .date(b.getCreatedAt().toLocalDate().format(dateFormatter))
                        .type("BADGE")
                        .badgeCode(b.getBadgeCode())
                        .badgeName(b.getBadgeName())
                        .build());
            }
        }
        result.sort((a, b) -> b.getDate().compareTo(a.getDate())); // 최신순 정렬
        return result;
    }
} 