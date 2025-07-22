package com.storycraft.reward.repository;

import com.storycraft.reward.entity.RewardBadge;
import com.storycraft.profile.entity.ChildProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RewardBadgeRepository extends JpaRepository<RewardBadge, Long> {
    List<RewardBadge> findAllByChildAndCreatedAtBetween(ChildProfile child, LocalDateTime from, LocalDateTime to);
    boolean existsByChildAndBadgeCode(ChildProfile child, String badgeCode);
    List<RewardBadge> findAllByChild(ChildProfile child);
} 